// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: BytesMsg.proto

package org.jyg.gameserver.core.proto;

public final class BytesMsgOuterClass {
  private BytesMsgOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_jyg_gameserver_core_proto_BytesMsg_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_jyg_gameserver_core_proto_BytesMsg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016BytesMsg.proto\022\035org.jyg.gameserver.cor" +
      "e.proto\"=\n\010BytesMsg\022\r\n\005msgId\030\001 \001(\005\022\021\n\tse" +
      "ssionId\030\002 \001(\003\022\017\n\007msgData\030\003 \001(\014B!\n\035org.jy" +
      "g.gameserver.core.protoP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_org_jyg_gameserver_core_proto_BytesMsg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_jyg_gameserver_core_proto_BytesMsg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_jyg_gameserver_core_proto_BytesMsg_descriptor,
        new java.lang.String[] { "MsgId", "SessionId", "MsgData", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}