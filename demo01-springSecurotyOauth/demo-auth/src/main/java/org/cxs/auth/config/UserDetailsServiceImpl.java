package org.cxs.auth.config;

import org.cxs.auth.util.UserJwt;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*****
 * 自定义授权认证类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // @Autowired
    // ClientDetailsService clientDetailsService;

   /* @Autowired
    private UserFeign userFeign;*/

    /****
     * 自定义授权认证
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // //取出身份，如果身份为空说明没有认证
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        // if(authentication==null){
        //     ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
        //     if(clientDetails!=null){
        //         //秘钥
        //         String clientSecret = clientDetails.getClientSecret();
        //         //静态方式
        //         return new User(username,new BCryptPasswordEncoder().encode(clientSecret), AuthorityUtils.commaSeparatedStringToAuthorityList(""));
        //         //数据库查找方式
        //         // return new User(username,clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
        //     }
        // }

        if (StringUtils.isEmpty(username)) {
            return null;
        }
        // 静态指定密码，以后改成通过name到数据库取
        String pwd = new BCryptPasswordEncoder().encode("123");
        // 调用feign
        //Result<com.changgou.user.pojo.User> user = userFeign.findById(username);

        //创建User对象
        String permissions = "admin";
        //UserJwt userDetails = new UserJwt(username,user.getData().getPassword(),AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        UserJwt userDetails = new UserJwt(username,pwd,AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        return userDetails;
    }
}
