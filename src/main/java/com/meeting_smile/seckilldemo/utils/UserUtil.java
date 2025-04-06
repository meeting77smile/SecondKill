package com.meeting_smile.seckilldemo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.valueobject.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 生成用户的工具类
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {//生成count个用户
        List<User> users = new ArrayList<>(count);
        //初始化count个user对象
        for(int i=0;i<count;i++){
            User user = new User();
            user.setId(13000000000L+i);
            user.setNickname("user"+i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDBPass("123456",user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");
        //利用jdbc插入数据库
        Connection conn = getConn();
        String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) value(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(int i=0;i<users.size();i++){
            User user = users.get(i);
            pstmt.setInt(1,user.getLoginCount());
            pstmt.setString(2,user.getNickname());
            pstmt.setTimestamp(3,new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4,user.getSalt());
            pstmt.setString(5,user.getPassword());
            pstmt.setLong(6,user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.clearParameters();
        conn.close();
        System.out.println("insert to db");
        //登录，生成UserTicket
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("E:\\360MoveData\\Users\\20548\\Desktop\\学习笔记\\后端\\秒杀系统\\config.txt");
        if(file.exists()){//如果文件已存在，则删除
            file.delete();
        }
        //涉及流方面的知识
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        raf.seek(0);
        for(int i=0;i<users.size();i++){
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");//设置请求方式
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() +"&password="+MD5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len=inputStream.read(buff)) >= 0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());//拿到响应结果
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response,RespBean.class);
            String userTicket = (String) respBean.getObj();//获取到userTicket
            //System.out.println("create userTicket :"+userTicket);
            String row = user.getId()+","+userTicket;//设置"config.txt"文件中每一行的内容
            raf.seek(raf.length());//设置长度
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());//换行
            //System.out.println("write to file :"+user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static Connection getConn() throws Exception {//用于连接数据库前的数据库配置
        String url = "jdbc:mysql://localhost:3306/seckill";
        String username = "root";
        String password = "2054865827";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);//生成5000个用户
    }
}
