package gg.scala.restapi;

import gg.scala.restapi.server.ServerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScalaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScalaApplication.class, args);
    }


}
