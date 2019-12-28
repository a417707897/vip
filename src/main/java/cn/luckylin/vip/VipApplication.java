package cn.luckylin.vip;

import cn.luckylin.vip.common.BaseMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "cn.luckylin.vip.mapper", markerInterface = BaseMapper.class)
public class VipApplication {

    public static void main(String[] args) {
        SpringApplication.run(VipApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
