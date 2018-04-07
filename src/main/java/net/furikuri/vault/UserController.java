package net.furikuri.vault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @Autowired
  private DbUserService dbUserService;

  @RequestMapping("/")
  public String index() {
    return dbUserService.currentUser();
  }

}