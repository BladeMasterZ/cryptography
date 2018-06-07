/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zhangxian
 */
public class Installer {
    private String id;
    private String pwd;
    private String sid;
    private String name;
    private String title;
    private String hashPwd;
    private String salt;

    
    public Installer(String id1, String pwd1,String sid1,
            String name1,String title1,String hashPwd1,String salt1){
        
        pwd = pwd1;
        sid = sid1;
        title = title1;
        name = name1;
        id = id1;
        hashPwd = hashPwd1;
        salt =salt1;
        
    
    }

    Installer() {
        //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }

    /**
     * @param sid the sid to set
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
        public String getHashPwd() {
        return hashPwd;
    }

    /**
     * @param hashPwd the title to set
     */
    public void setHashPwd(String hashPwd) {
        this.hashPwd = hashPwd;
    }
    
     public void setSalt(String salt) {
        this.salt = salt;
    }
     

    

    public String getSalt() {
        return salt;
    }
    

}
