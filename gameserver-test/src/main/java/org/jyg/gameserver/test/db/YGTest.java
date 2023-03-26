package org.jyg.gameserver.test.db;

import cn.hutool.core.codec.Base64Decoder;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.jyg.gameserver.core.startup.HttpClient;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ExecTimeUtil;
import org.jyg.gameserver.db.DBConfig;
import org.jyg.gameserver.db.util.CreateTableUtil;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.LockSupport;

public class YGTest {

    public static void main(String[] args) throws Exception{
            // delete the database named 'test' in the user home directory
//            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test");
            Statement stat = conn.createStatement();

            // this line would initialize the database
            // from the SQL script file 'init.sql'
            // stat.execute("runscript from 'init.sql'");

//            stat.execute("create table test(id int primary key, name varchar(255))");
            stat.execute("insert into test values(2, 'Hello')");
            ResultSet rs;
            rs = stat.executeQuery("select * from test");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            stat.close();
            conn.close();


        JSONObject jsonObject = new JSONObject();

        jsonObject.put(",","11");

        AllUtil.println(jsonObject.toJSONString());

    }

    public static String sbformat = "<html><head></head><body><div align='center'><h1>500 Internal Server Error</h1></div>" +
            "<details class=\"menu\" > <summary>errorMsg</summary> <ul><li>" +
            "<pre>" +
            "%s" +
            "</pre>" +
            "</li>" +
            "</ul>\n" +
            "</details>" +
            "<body></html>";

    public static int i = 0;

    @Test
    public void tring()throws Exception {

        String errorMsg = "2222";
        StringBuilder sbbbb = new StringBuilder(200);


        ExecTimeUtil.exec("append",()->{

            for(int i = 0;i<10000000;i++){

                StringBuilder sbb = new StringBuilder(200);
                sbb.append("<html><head></head><body><div align='center'><h1>500 Internal Server Error</h1></div>" +
                        "<details class=\"menu\" > <summary>errorMsg</summary> <ul><li>" +
                        "<pre>").append(errorMsg).append("</pre>" +
                        "</li>" +
                        "</ul>\n" +
                        "</details>" +
                        "<body></html>");

                String sb = sbb.toString()
                        ;
                i+= sb.length();
            }

        });

        ExecTimeUtil.exec("format",()->{

            for(int i = 0;i<10000000;i++){

                String sb = String.format(sbformat , errorMsg);
                i+= sb.length();
            }

        });



        ExecTimeUtil.exec("plus",()->{

            for(int i = 0;i<10000000;i++){

                String sb = "<html><head></head><body><div align='center'><h1>500 Internal Server Error</h1></div>" +
                        "<details class=\"menu\" > <summary>errorMsg</summary> <ul><li>" +
                        "<pre>" +
                        errorMsg +
                        "</pre>" +
                        "</li>" +
                        "</ul>\n" +
                        "</details>" +
                        "<body></html>";
                i+= sb.length();
            }

        });







    }


    @Test
    public void testShift(){
        AllUtil.println(10 << 1 + 10 << 1);

        String s = "";


        byte[] bs = new byte[4];
        bs[0] = 0;
        bs[1] = 63;


        List<Long> waitPlayers = new ArrayList<>();
        waitPlayers.add(100L);
        waitPlayers.add(1000L);
        waitPlayers.add(1020L);
        waitPlayers.sort(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return Long.compare(o1 , o2);
            }
        });


        AllUtil.println(Math.sqrt(100));

    }



    @Test
    public void testJson(){}{


        Map<String , Maik> maikMap = new HashMap<String , Maik>() {};

        Maik maik = new Maik();

        maik.setContent("2222");

        maikMap.put("111",maik);


        String jsonStr = JSONObject.toJSONString(maikMap);


        AllUtil.println(JSONObject.parseObject(jsonStr , MaikMap.class).getClass());

        Map<String,Maik> maikNewMap = JSONObject.parseObject(jsonStr , new TypeReference<Map<String,Maik>>(){});


        System.out.println(maikNewMap.get("111").getClass());

    }


    public static class MaikMap extends HashMap<String,Maik>{

    }



    @Test
    public void testExist() throws Exception {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        dbConfig.setUsername("root");
        dbConfig.setPassword("123456");


        boolean isTableExist = CreateTableUtil.isTableExist(Maik.class);

        AllUtil.println("isTableExist : " + isTableExist);

    }

    @Test
    public void testPark() throws Exception {
        ExecTimeUtil.exec(",",()->{

            LockSupport.parkNanos(1);

        });

        ExecTimeUtil.exec(",",()->{

            LockSupport.parkNanos(1);

        });
    }


    @Test
    public void testZip() throws Exception {

        String str = "UEsDBAoAAAgAACF5lVUAAAAAAAAAAAAAAAAFAAAAY3Jvbi9QSwMEFAAACAgAIXmVVW7Hp+BzBQAA0RIAABIAAABjcm9uL0Nyb25UZXN0LmphdmG9WM1uGzcQvhfoOxB7omKFktZx+qO6jWM7TQonci03PTSFQe/SKyqr5ZbkylYDAz0VRdFD732BoJceChRp+zpxn6PD/RN3tbJlIwkFSMshZ+abGXJmVjH1ntOAISEDMp4FJKATppicMkk0U5p4UkT999/LPnwSC6mRF5FRooUIYXUWa0HUbDJhWnKPHGzf6Zf7UplJxDU5BFE1elWXJyQjieYh2QrDr+B3xd27Z8w75BO2yPJdQqX+ntxqpAIlJEPtD70R85OQyQfU00LO+paVYzql4IMzTfapVGz3zGOx5qk37B0pjB2qWRN9X4qYSc2ZSiXHyXHIPeSFVCm0DZ41fkEvMuciGPkGpamGn6ngPppQHuEhODcKvvkWURmoFtIjKU4VquJqo9KakmbLNiMUUYAicYo20XCmNJsQL5GSRdr48DEPQ65wq29zGMuQb742UcRO0zkGCa1+VbJxLo0pICChCAJAa37vjK1YGYN3z2LJlDLQYNlQcrnX5sfOLbSBNjq9Lup1XNRrf4A+c6rYrYDXdGfUmvrGrdgBBbfyT6Gg5p4IDokLguZSScD0E6A+pSH3jW+3TjST2PjRSFjgXgfu3B9XsNrK7cNPGEywk0EQkcecNm7d/vTFfLcZJ0JiHmnEN7t9/onbhe+1tVZtUwXZTcwy43w+Pa9GZSnqFRD3uma8M9CXQN4bfP5G8a4W/cs9bD6dTkHsdC5++u3i339ev/rh9avfVZEb7C1lwkDlsskMi3nRwNphJzQJdbmUJoq5rFKC2bstIpM5cYvEicaOes5mThs5akrDhDkZ43Ksh5IHQRVpTkI6/90sKPcTHvqgFC5xTsEtm7EY5JTr0SMfUh3XM+zkcnoGVSBFEvecZrZEQSr6QhxDrCiwpQx6umy3UVI4CA9NlWHFtACqKlTwUAoM3CXBOY+iIfNE5Cu83qigVCRZzKh+ALlxaixukWMjvhaS3EgTkNyCxzQuYqLdzBT3ynCMxbG9aEQxTXmIYAECAXMrCDDDD1kYCnggaaFbxa/j1K/jS/06D95kBpoNw2SWxs5pNh82NZs+djNtzab/9+fLix9/yX138fdfF7/+cfHzy8b7Mz/zxZMxH/S2c/ZlVwQKvNR4fmvN6rk9AaZIQHbQI65QTE33coI84TMkkwjxCAkVcCBEEIjIgDJM90w7UWkk0g7C9HFfpukQl61DrUMoEGZ9BlIQVge9eObE8TPn4/Vul3TbMJmaycZHxL3rfugCIUomQEmXODzcvdODR5Wy3M1Y+JnZcO7068kp7z4U03mLNMNNaWf/YLB/dPjwYHdr52h/MNg72t7bGg7bQwAZssORZNTfT/tQc9DS3AlNIlwHK1UOjsfM09AMoM1caWApdazCr+fyssdtkUTasYU1wF5BQttZzxuHd+kAO6oLgpE6yTufhkVcbzRqdQI41YlRVq0FlWNulvc42Ain8zGNaJqYCfV9uCEFHRv99ryFalXy3gDSm+Q+q5Lz052f1tLqOrcZkulERgjOahg6ixX0OurSywSX+1DcZ6YjSDTzMcDPnuEu5VXP7KnTGrHlrzokBjN0CK1mTbjTUPJvArgE85Rp8RYw1+S/KdhfU3VdN7eRvXGe5NjKtlhKm+woH+21as+3UbR7VeYblM06XjNqpRBkHTlrfM2Bb8e8q9sNjVUVbRFL6yJ0mRSSVQ/GbRBa57MS3TXbsRUMySWBWtuAG/RTzdqqWhe6Lbe12FGlNfqJOMXL/JjluXrlR1bptw6OXdwt5rIPKOhZTicqZCzG2etDdzG5qlGifXEaYS2T8sWg0kBc0gsM4O0QL/kTofjD4G2+RCO367qVy1W/iFe+vJX/RLRaFevh639QSwMEFAAACAgAnF2TVd8W4HwjAwAAkQkAABsAAABjcm9uL0dsb2JhbENyb25NYW5hZ2VyLmphdmG9VV1v2jAUfUfiP1h5cgZyN+2RUq2jXdepLaiwZ2QcE9yaOLMdWDr1v+8a8mFC2vE0CxHHvh/nHPvepJQ905gjpWPylMckpmtuuN5wTSw3ljCtkkG30+2Idaq0bbNjSnOypgmE0eROLDnLmeSDf7pkVkhyKeVPeJ5ofQOLI5VY/tseevzKqLYv5EPrKqxIMrXRlK14lEmuv1Fmlc59Xk90QwtEWtP8ThgvQ71ZrHc7abaQgiEmqTHoRqoFlSOQ6n4vA3Ip+Zon1qBKEfTHOXY7CEaqxYZajipIyLjZ4NjCo4ziA/qHdg7Z+X3uQMyoeb5ArJgZNEQJ36KK1/kFDmv3PY8jBviNvKFjgYphV8IQbxMyHUJ0Rq8ep30ykydsBanEC4/QRokI0SgqgeOaQ0Uh9HKKJfZyEGGmFg4Zh77NHptW2x3xWyl5TCXYWX79m/HUCpXggErNaZQj4/yDcFC7v9bTSkQCEHGF55jalzHcVC0ifsB0R87sAZbHf3ZWBp/mxvI1MdxOtEq5tjluuaVk8jiezGffH68vr+aT8fhuPrq7nE77U5HEks9WjsVEKUl2d5HE3D6APiDI4EC0IlnsJQu8GrF1nP10pLIEZEFDuD2ZlE15W7CfEK6Pgs9Nqf+vInXqlsDILItiadnEh+4WzBua7EoYApily1n5NxzdWCqNkUgsEsOPA3Fe3zIDNQH2otdDTcXdaKkNyFe7Q1osfH3L8UMtrrilQqIntQAXeP+aCRlBdwW68FaUHcz2qoXHMcpBtsKubiNobsKdOgScBz3RC+C/ROKAuHA4hBOPtcrST0FIFi5jixpuQBTn5HBSS+9pikOSZhYHZcig36y+k9y9XhH0/T7WisK7wTMtYtfJbfEcomLF061YwaeLVUQ7UTDnW14j7LbLlxID89bwcax3cIVk15Ye1Bb7B9M0211pYooU7p6A0v1SlKaH1w8bAfYt0C98xKhlK4Srjox42Cyouoc/QvcQa69986Mu4rVkN3m3IasUH37I3ihmYlaZjdQ2wVZnvBV/VebvEeEEPtOJhU8Qe55pyjhu+d7AA35/AVBLAwQUAAAICABOVpNVlJOewCABAABAAgAAEgAAAGNyb24vSGVsbG9Kb2IuamF2YYVR20rEMBB9L/Qfhj61FILPFkHRBRHUB/UDsunQZk2TOp2uVfHfnd6QxZUdApmZnHM4M2m1edUVQqBK7T4qVekGO6Q9kmLsWBkKvoijOLJNG4iP4UwgVD1bp66ce5G7OEC/9Zr4U92F7X/9zYBG+MFfB8848EncZjDYjsnkrO23zhowTncd3KJzQbAgCg4b9NzBWH6NyDgCiQW/ItNsfoQlrGewcAFnxdr9XpPLR5mXbIkHQvtgS8DJG6ZHxgEz3xlwTeF98vN3lNHEauGJyfoKSs0oRha6qpCFeYOsrUuztdSs73U71zMvTYSok6z4FVw+RrXyzs6nST0ODztZzHkCOTzXhLpUpieSjc3VLPkg/ywbyiEpknwytOrKVuT8AFBLAwQUAAAICABoW5NV3UYDvFEBAAA9AwAAEwAAAGNyb24vTXlDcm9uSm9iLmphdmGVUt1LwzAQfx/sfzj2lIIE34cgzqoTp6LTV8myo8a1Sc3HPhT/dy/tttY5GYZC7y6/j8txpZAzkSEYm/G3VcYzUaBDO0fLPTrPpTW63+10O6oojfX7cNJY5KhD4Xg6R+3HqxL7hwkRytMlyuDFJMeKepgWvMr5JRUHRntc7jDeg7D+g1+byV/12lAZfYC/xaVLiWUMqimUYZIrCTIXzsFoNaDpEBiANHIs6AUOYv4ZsUDn9I46t2qKdbqmz42aAlYOyPY0BbL+J+BfrVlUmr8balziqZsZCzeDoglPgDUXyVqWZ+hJ8Ry9UDlLNqnwYiTKOmc9uSb1kn5j0ho8ZK2YbFpX//JpyfywatUjkEIXCrQjoWldLbHrWVZrw5oXR+yFNcUGP5yy5Ai2e8nvn85uho9XL+lzejs+Ao0L2NnBHbGHoHW8YwnpHG86/Op26PsGUEsDBBQAAAgIADlYk1Uhsdwv1AAAAEQCAAAUAAAAY3Jvbi9NeUNyb25UYXNrLmphdmFtkMEKgzAMhu8D3yFHB9IXkJ2EwQ67bHuB6rKuU6ukrSBj776WqbNq6CH0T/58ScuLkguEhgR79YIJXqNG6pCYQW1YQY1Ko120a21eyQKKimsN5z5z/zeuS3h7EVy0JDtuEKQy8KCmzhqlbY10uqfLkqshqQRM3qF6sUrxvEKgIZnX/Cj+8+P1uGTun6zt9p4ZhjBPqVnYD4c1f1DufV3RgB9I4wwnz+i9/Fms4LkFmmMwKQ7YCI0ltQ2ztBtWdo7+Mps+M95l93Qj1z/mmx7rpdz7AlBLAwQUAAAICABokoxVi5j/upwAAADoAAAAGgAAAGNyb24vU2luZ2xlVGhyZWFkUG9vbC5qYXZhXYw9D4IwEIb3Jv0PN0JiujjyCxgUAixOppZLRSut12L8iP9dCgzqm8sN7z3POanOUiNY0uL00ELLC3qkG5II6INQZPuMM866i7MUJu46SApP4cfKiDpubI6Esi2tNRPshoPpFCgjvYe66/U3AXgP2Lfx8KvCK7qcwZjlw7+bpBGCJX5wSMl6BTMgtkW12ZdVXlR5s0uzGXxzNs4HUEsDBBQAAAgIAGWHk1XoA1QSxwQAAMYSAAAOAAAARXhjZWxVdGlsLmphdmHNV1tv2zYUfg+Q/8D5iW4yxna2dq2TYEUvgIshKebsAix7YGTKZkKLAkklzgr/95GURVISpTjYywg0lcnzfTw3nkPmOLnHSwK4WKK7pyVa4jWRRDwQgRSRCpFNQtj08ODwgK5zLhRIMrQqFOcMJVwQRDn6TBn5TVEj1Skz4w0JvkaY0Vt8i1GKpbqTPENf5leX74XAT3vIXd3ekUR5QaM/znGyIijnFEmJCm3Gmi8IQx8IY/tJ/sof9xOcrwjZc/c/uLi/5fy+U3ojZRrI/zmff+7VOCLfp3dEvF/7CCBuw12RUYWudZqECXKHH7AJ+atpfarQCWAnDw/y4pbRBCQMSwk+mQwz2QG+mTWgx25dKqz0fw+cLoAJ/DW3svAXKtWZT4ILu1h+m6VjMFeCZktgc9dk51esVm5WGuMvdZoPgVoJ/ijB7Mrw5oryzOgAdsOlo+Uvv85BRh79ChxOvbyTQnixeM8YrKs1nFbmVcKVPQ543FTZ6+rA2z2dFNH+vzom1J+mwOuNJP2HwCE4A+Nh6EEzLJX12owxssTsvVgWa5IpRw0HMaJB6NptfXMfe5BSwhZG6S+aQ4fHUy2J8nJwFNLZBCqtvvAMchddC7ciF7BGj+7J05woOCyj4fmMK68KlRdKkxK8Bik3XIU9xKHQLAtkqGwLVQcNPFYfTsQLKfHU9LIJR1WHdc3WysNakIetuJixU0Hb3FCuAZ62oaF+Gh8WCe002YRsAWGSxHToI2qx1H+WXt5pH/q/rX7oPjNsBSwzXnNUOqBEoxWxizBy9qoRMUNXYO1OIZX5OC+Zd3R6pp5+Xn8BaWaaHJtlC7LRuNHU/TgLMnN3Ltzi0REYRpSwlpWH2WE1acCjDwWsSGIqmWGaD0jMn3Nn0s4Us/Qs3kCRJMoI/45ZQfwpikG2rRg33V35SReSnZ9G0+r7rFk73EqHj4La4euzthT6hWG9isCKsZ0IZpiAi2jQnb5Hui5Gsf/TDHC1lZGFjeCusJbzlqI3pGaEWSRelkA0hcHW35UVsMvaEgBCBM10U8wSwlMwyxRZEhGtfuFo5yx0UM/cpbAZ3RWufx/PjhQvQ1c2mM6N4kvbWH5FDlNLzhW/R0GV1oc3N085SlkhV7Cew5ro5MTZhVWyAtBfF4iJl183w/UnfauEVdlutqlpE1XeH0htPnBBSjPMWLQf6vZQ5U4sLOWTBCWMy5jV2yahaWt9yVjni7TAtoau9+2tZ4XoId8G98TOm6L1+jU3txobhd6LIS/U10J9pMKG6Jlbs7mcwOg1ptndG0166O//znRFBFZcnNmOfFEW2Goy7Nu1hda1QVdZAIOOP60ToRWWl2Sj73VTEI1AdU2oo7ISEjt2ZSPQZdK1/r5otW72P5snVS10NmbmPf7BVlG4zw09/nxxsyjHQpLyFTP469vNQD9aPm3ym8G705EexzcDRrN7be16nunJ8Xg0GY9Ozby0v/WHesqJ+dwet+Bd+HGFnzyD7yKYVASnnQTjLvz47WjsDPjh5QYYvNv/x97949q/cbu/9ujJXmi99+hthX7Tif7+dYffTivsTx57uv17UE/OeIkefHx3cqLWufk3QRsmN4PhXq/ZCFBPFmlSf929FD1xaHtk9J9/AVBLAwQUAAAICAD5jJdVndBbcn4DAAAFCgAACwAAAEpES0Flcy5qYXZhtVXdThNBFL4n4R2OvSBbgrtRIyFpSCxt1Yr8hJYLYwwZtsN2Zbu7mZkWGiXRCwQRgomGaKIRohGMMcQ7QH0bluJbONPZbrvbUuDCszczO2fOz3e+c8ZF+jwyMDjEUB9XDdVAJUwxqWCiMkyZqpOqyxK9Pb09Zsl1CIPHqIIW5W9HTZluEZNE58NxJ1fWi5OoUDBtI7OoY5eZjn2Gcg7rBLNRXD3jnLpYV7OVSUR4gAyTHN93Uw3stSuqtumoehERipmaY8guIFJIyT2NqFKsl4nJqmrWriDLLCQtw+H7YikI5IzE2m7yUM5VlYgFPlr1e3vc8qxl6qBbiFK4lx5NYgpPxIH4gIvW3y8X0A/e2vbp7mdv/0Xt16fjg2e131u17ecnW4fe782GjiYXLjEriGGYM21kAWWIcSc5RnjNIDOemkhnx+/AMMTKbO7qUCzR1dmlHSTv35mYyubvjoFwkczkOjrg9oXxvR1u3Ntc83a+e8vLl/Y1mcznM1PjviMtkxrR3Hmd3nQlQYVnTbukkdRIqs1IuwnZJYDtOkGDnumiWsAdVSUDZO2V2SrDDx/BPK7GBQ/AF54Dj0xC9ndlpfZ2rzVAiV1I21v9crL1g9Pz5N2+9/rr8cHRtcHjPxsNNISYc6BwP6qFbYMV4cow3LgOfX0Q/ndtsB5I85oQViTOAth4AaZtWnYF6XFhwsUECWIHDFdiF7Efiyda7S8182Ck2gqCkGACAA1Ww/VIQrNBJDbQJGI8QLshobJxC3KhGphlbU4OW8eKz4p4InxT0zj6vAze+ntv9aO3+8pb35K3vf3D0587Xfyopm0yxV/zNpx6MJmfGZtIZwaa2chQw0YiExLMyF4CENFSYtcjEhPpjXCCUSU6INXp/O2ZoXg7TiHO/kecQn5COKUzF8JpCXTE9CIoZ41beAqd3y5+0GGa89LFo+S7KO1xKxZLjSiXOk3ZoKuDP7fqXigE5qLTUM4Lf1D4/GrMjRI16gWOQ9RKuIn93odhTp2yZXVo8VyVMlxSnTJT+TCzmRLj6PAxUvt2JG5EmlYIr0uZ2HV70YYOl0rTTl7unu6s+y/Am43jPx9aL4RTSyOGOPF0yYaCc1uMVMW1kGnn8SJrcrrxrsVb0feDCrdhw0iAVqKlQh1rJF/CZo1c0WkB2sGia6l8il+sVJH4w+1xTvzi48t/UEsBAj8ACgAACAAAIXmVVQAAAAAAAAAAAAAAAAUAJAAAAAAAAAAQAAAAAAAAAGNyb24vCgAgAAAAAAABABgAQ5k4GgsV2QF5WNwaCxXZAVjvcGvTDdkBUEsBAj8AFAAACAgAIXmVVW7Hp+BzBQAA0RIAABIAJAAAAAAAAAAgAAAAIwAAAGNyb24vQ3JvblRlc3QuamF2YQoAIAAAAAAAAQAYAFdyOBoLFdkBV3I4GgsV2QGqm6pw0w3ZAVBLAQI/ABQAAAgIAJxdk1XfFuB8IwMAAJEJAAAbACQAAAAAAAAAIAAAAMYFAABjcm9uL0dsb2JhbENyb25NYW5hZ2VyLmphdmEKACAAAAAAAAEAGADYcZdBXBPZAdhxl0FcE9kBKSKSJU8T2QFQSwECPwAUAAAICABOVpNVlJOewCABAABAAgAAEgAkAAAAAAAAACAAAAAiCQAAY3Jvbi9IZWxsb0pvYi5qYXZhCgAgAAAAAAABABgAzlBBplQT2QHOUEGmVBPZAYsxJNfcDdkBUEsBAj8AFAAACAgAaFuTVd1GA7xRAQAAPQMAABMAJAAAAAAAAAAgAAAAcgoAAGNyb24vTXlDcm9uSm9iLmphdmEKACAAAAAAAAEAGACcy2nKWRPZAZzLacpZE9kBpuo6rk8T2QFQSwECPwAUAAAICAA5WJNVIbHcL9QAAABEAgAAFAAkAAAAAAAAACAAAAD0CwAAY3Jvbi9NeUNyb25UYXNrLmphdmEKACAAAAAAAAEAGABHB/Y8VhPZAUcH9jxWE9kBjOUEDFUT2QFQSwECPwAUAAAICABokoxVi5j/upwAAADoAAAAGgAkAAAAAAAAACAAAAD6DAAAY3Jvbi9TaW5nbGVUaHJlYWRQb29sLmphdmEKACAAAAAAAAEAGACnD9AvEw7ZAacP0C8TDtkBplKS3hAO2QFQSwECPwAUAAAICABlh5NV6ANUEscEAADGEgAADgAkAAAAAAAAACAAAADODQAARXhjZWxVdGlsLmphdmEKACAAAAAAAAEAGAAT7S4oiBPZARPtLiiIE9kBTa0W8nsT2QFQSwECPwAUAAAICAD5jJdVndBbcn4DAAAFCgAACwAkAAAAAAAAACAAAADBEgAASkRLQWVzLmphdmEKACAAAAAAAAEAGAAi1wqAshbZASLXCoCyFtkBAmjq4nQW2QFQSwUGAAAAAAkACQCAAwAAaBYAAAAA";

        FileOutputStream outputStream = new FileOutputStream("D:/tmp/code.zip");

        outputStream.write(Base64Decoder.decode(str));
        outputStream.close();

    }



}
