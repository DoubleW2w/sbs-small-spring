package org.springframework.test.beans.factory.bsafb;

import lombok.Getter;
import lombok.Setter;

/**
 * BSAFB 是 bean-scope-and-factory-bean 分支名称的首字母
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
@Setter
@Getter
public class BSAFBUserService {
  private String uId;
  private String company;
  private String location;
  private BSAFBIUserDao userDao;

  public String queryUserInfo() {
    return userDao.queryUserName(uId) + "," + company + "," + location;
  }
}
