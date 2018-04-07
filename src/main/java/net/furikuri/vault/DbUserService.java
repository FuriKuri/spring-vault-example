package net.furikuri.vault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@RefreshScope
public class DbUserService {

  @Autowired
  private DataSource dataSource;

  @PostConstruct
  @Scheduled(fixedDelay = 60000)
  public void postConstruct() {
    System.out.println("Current connection user: " + currentUser());
  }

  public String currentUser() {
    String currentUser;

    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {

      ResultSet resultSet = statement.executeQuery("SELECT CURRENT_USER();");
      resultSet.next();
      currentUser = resultSet.getString(1);
      resultSet.close();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return currentUser;
  }

}
