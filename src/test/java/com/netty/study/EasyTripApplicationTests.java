package com.netty.study;

import com.netty.study.NettyServerApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@SpringBootTest(classes = NettyServerApplication.class)
@RunWith(value = SpringRunner.class)
@ActiveProfiles("dev")
class EasyTripApplicationTests {


    @Resource
    private RedisTemplate redisTemplate;


    @Test
    void contextLoads() {


        redisTemplate.execute((RedisCallback) connection -> {
            try {
                String channel = "guangbo";
                String message = "开始抢购啦。。。";
                Long publish = connection.publish(channel.getBytes("UTF-8"), message.getBytes("UTF-8"));
                return publish;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        });

        redisTemplate.execute((RedisCallback) connection -> {
            connection.subscribe((message, pattern) -> {
                byte[] channel = message.getChannel();
                byte[] body = message.getBody();
                System.out.format("channel : %s ,message : %s", new String(channel), new String(body));
            });

            return null;
        });
    }


    /* 截屏screenshot
     * 参数：图片存放的位置
     * 返回： 生成图片生成的路径，图片格式是png，也可以根据自己的需要进行修改
     */
    public static String screenShot(String fileName) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        String randomName = UUID.randomUUID().toString().replace("-", "");
        String name = randomName + ".png";
        // 截图保存的路径
        File screenFile = new File(fileName);
        Robot robot;
        String path = "";
        boolean b = false;
        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            File f = new File(screenFile, name);
            b = ImageIO.write(image, "png", f);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (b) {
            path = fileName + File.separator + name;
        }
        return path;
    }

    public static void main(String[] args) {
        screenShot("D:\\dev\\JavaStudy\\easy-trip-service\\easy-trip-service");
    }


}
