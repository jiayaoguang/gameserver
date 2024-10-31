package org.jyg.gameserver.core.handle;
/**
 * created by jiayaoguang at 2017年12月6日
 */

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;
import org.jyg.gameserver.core.util.Logs;

/**
 * A simple handler that serves incoming HTTP requests to send their respective
 * HTTP responses. It also implements {@code 'If-Modified-Since'} header to take
 * advantage of browser cache, as described in
 * <a href="http://tools.ietf.org/html/rfc2616#section-14.25">RFC 2616</a>.
 *
 * <h3>How Browser Caching Works</h3>
 *
 * Web browser caching works with HTTP headers as illustrated by the following
 * sample:
 * <ol>
 * <li>Request #1 returns the content of {@code /file1.txt}.</li>
 * <li>Contents of {@code /file1.txt} is cached by the browser.</li>
 * <li>Request #2 for {@code /file1.txt} does not return the contents of the
 * file again. Rather, a 304 Not Modified is returned. This tells the browser to
 * use the contents stored in its cache.</li>
 * <li>The server knows the file has not been modified because the
 * {@code If-Modified-Since} date is the same as the file's last modified
 * date.</li>
 * </ol>
 *
 * <pre>
 * Request #1 Headers
 * ===================
 * GET /file1.txt HTTP/1.1
 *
 * Response #1 Headers
 * ===================
 * HTTP/1.1 200 OK
 * Date:               Tue, 01 Mar 2011 22:44:26 GMT
 * Last-Modified:      Wed, 30 Jun 2010 21:36:48 GMT
 * Expires:            Tue, 01 Mar 2012 22:44:26 GMT
 * Cache-Control:      private, max-age=31536000
 *
 * Request #2 Headers
 * ===================
 * GET /file1.txt HTTP/1.1
 * If-Modified-Since:  Wed, 30 Jun 2010 21:36:48 GMT
 *
 * Response #2 Headers
 * ===================
 * HTTP/1.1 304 Not Modified
 * Date:               Tue, 01 Mar 2011 22:44:28 GMT
 *
 * </pre>
 */
public class HttpStaticFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
	public static final int HTTP_CACHE_SECONDS = 60;

	@Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if (!request.decoderResult().isSuccess()) {
			sendError(ctx, BAD_REQUEST);
			return;
		}

//		String s = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
//
//		System.out.println("CONTENT_TYPE: " + s + "," + request.headers());



		//包含点字符的当做静态文件请求处理


		String noParamUri = getNoParamUri(request.uri());

		if( noParamUri.indexOf('.')==-1){
			if (!request.uri().endsWith("/")) {
				request.retain();
				ctx.fireChannelRead(request);
//				Logs.DEFAULT_LOGGER.info("static :" + request.refCnt());
				return;
			}
		}

		if (request.method() != GET) {
			Logs.DEFAULT_LOGGER.info("request.method() != GET : {}" , request.uri());
			sendError(ctx, METHOD_NOT_ALLOWED);
			return;
		}

		String httpRootDir = System.getProperty("http.root.dir","/html");

		final String uri = httpRootDir + request.uri();

		final String path = sanitizeUri(uri);
		if (path == null) {
			sendError(ctx, FORBIDDEN);
			return;
		}

		Logs.DEFAULT_LOGGER.info(uri);

		File file = new File( path);
		if (file.isHidden() || !file.exists()) {
			sendError(ctx, NOT_FOUND);
			return;
		}

		if (file.isDirectory()) {
			if (uri.endsWith("/")) {
				sendListing(ctx, file, uri);
			} else {
				sendRedirect(ctx, uri + '/');
			}
			return;
		}

		if (!file.isFile()) {
			sendError(ctx, FORBIDDEN);
			return;
		}

		// Cache Validation
		String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
		if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
			Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

			// Only compare up to the second because the datetime format we send to the
			// client
			// does not have milliseconds
			long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
			long fileLastModifiedSeconds = file.lastModified() / 1000;
			if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
				sendNotModified(ctx);
				return;
			}
		}

		try(RandomAccessFile raf = new RandomAccessFile(file, "r");){

			long fileLength = raf.length();

			HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
			HttpUtil.setContentLength(response, fileLength);
			setContentTypeHeader(response, file);
			setDateAndCacheHeaders(response, file);
			if (HttpUtil.isKeepAlive(request)) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			// Write the initial line and the header.
			ctx.write(response);

			// Write the content.
			ChannelFuture sendFileFuture;
			ChannelFuture lastContentFuture;
			if (ctx.pipeline().get(SslHandler.class) == null) {
				sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength),
						ctx.newProgressivePromise());
				// Write the end marker.
				lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
			} else {
				sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
						ctx.newProgressivePromise());
				// HttpChunkedInput will write the end marker (LastHttpContent) for us.
				lastContentFuture = sendFileFuture;
			}

			sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
				@Override
				public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
					if (total < 0) { // total unknown
						Logs.DEFAULT_LOGGER.info(" {} Transfer progress: {}" ,future.channel() ,  progress);
					} else {
						Logs.DEFAULT_LOGGER.info(" {} Transfer progress: {} / {}" ,future.channel() ,  progress , total);
					}
				}

				@Override
				public void operationComplete(ChannelProgressiveFuture future) {
					Logs.DEFAULT_LOGGER.info( " {} Transfer complete.",future.channel());
				}
			});



			// Decide whether to close the connection or not.
			if (!HttpUtil.isKeepAlive(request)) {
				// Close the connection when the whole content is written out.
				lastContentFuture.addListener(ChannelFutureListener.CLOSE);
			}
		}catch (FileNotFoundException ignore) {
			sendError(ctx, NOT_FOUND);
			return;
		}


	}

	private String getNoParamUri(String uri) {

		if(StringUtils.isEmpty(uri)){
			return uri;
		}

		String noParamUri = uri;

		if (uri.contains("?")) {
			noParamUri = uri.substring(0, uri.indexOf('?'));
		}
		return noParamUri;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Logs.DEFAULT_LOGGER.error("make exception : " ,cause);
		if (ctx.channel().isActive()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}

	private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

	private static String sanitizeUri(String uri) {
		// Decode the path.
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}

		if (uri.isEmpty() || uri.charAt(0) != '/') {
			return null;
		}

		// Convert file separators.
		uri = uri.replace('/', File.separatorChar);

		// Simplistic dumb security check.
		// You will have to do something serious in the production environment.
		if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || uri.charAt(0) == '.'
				|| uri.charAt(uri.length() - 1) == '.' || INSECURE_URI.matcher(uri).matches()) {
			return null;
		}

		// Convert to absolute path.
		return SystemPropertyUtil.get("user.dir") + File.separator + uri;
	}

	private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

	private static void sendListing(ChannelHandlerContext ctx, File dir, String dirPath) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

		StringBuilder buf = new StringBuilder().append("<!DOCTYPE html>\r\n")
				.append("<html><head><meta charset='utf-8' /><title>").append("Listing of: ").append(dirPath)
				.append("</title></head><body>\r\n")

				.append("<h3>Listing of: ").append(dirPath).append("</h3>\r\n")

				.append("<ul>").append("<li><a href=\"../\">..</a></li>\r\n");

		File[] childFiles = dir.listFiles();
		if(childFiles != null){
			for (int i = 0; i < childFiles.length; i++) {
				File f = childFiles[i];
				if (f.isHidden() || !f.canRead()) {
					continue;
				}


				String name = f.getName();
				if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
					continue;
				}
				if (f.isDirectory()) {
					name += "/";
				}

				buf.append("<li><a href=\"").append(name).append("\">").append(name).append("</a></li>\r\n");
			}
		}

		buf.append("</ul></body></html>\r\n");
		ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
		response.content().writeBytes(buffer);
		buffer.release();

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
		response.headers().set(HttpHeaderNames.LOCATION, newUri);

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
				Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * When file timestamp is the same as what the browser is sending up, send a
	 * "304 Not Modified"
	 *
	 * @param ctx
	 *            Context
	 */
	private static void sendNotModified(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
		setDateHeader(response);

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Sets the Date header for the HTTP response
	 *
	 * @param response
	 *            HTTP response
	 */
	private static void setDateHeader(FullHttpResponse response) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
		dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

		Calendar time = new GregorianCalendar();
		response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
	}

	/**
	 * Sets the Date and Cache headers for the HTTP Response
	 *
	 * @param response
	 *            HTTP response
	 * @param fileToCache
	 *            file to extract content type
	 */
	private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
		dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

		// Date header
		Calendar time = new GregorianCalendar();
		response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

		// Add cache headers
		time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
		response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
		response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
		response.headers().set(HttpHeaderNames.LAST_MODIFIED,
				dateFormatter.format(new Date(fileToCache.lastModified())));
	}

	/**
	 * Sets the content type header for the HTTP Response
	 *
	 * @param response
	 *            HTTP response
	 * @param file
	 *            file to extract content type
	 */
	private static void setContentTypeHeader(HttpResponse response, File file) {
		String fileName = file.getName();
		int pointIndex = fileName.lastIndexOf('.');
		if(pointIndex > 0 && pointIndex < fileName.length()){
			String fileSuffix = fileName.substring(pointIndex + 1);
			switch (fileSuffix) {
				case "html":
				case "htm":
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
					return;
				case "jpg":
				case "jpeg":
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/jpeg");
					return;
				case "png":
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/png");
					return;
				case "xml":
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/xml");
					return;
				case "json":
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
					return;
				case "docx":
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/msword");
					return;
				case "pdf":
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/pdf");
					return;
				default:
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
					return;
			}
		}



	}
}
