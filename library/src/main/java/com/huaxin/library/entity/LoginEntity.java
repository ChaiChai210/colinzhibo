package com.huaxin.library.entity;

public class LoginEntity {

    /**
     * token : eyJpdiI6Ik4wTWRBN3VWSnFqd1RlTE9DRnVWNXc9PSIsInZhbHVlIjoiWmh4cDRpdjU5MjlUazZiOThVNlJRSmVxVUsrQ0NUUnhRdE5SVzdKNklIZz0iLCJtYWMiOiI2OTY2MTBhYWE1ZTQxYTY1MThhNGUzMGRkMTg5ZjgwMTUxMDI2ZDI0ZmRjNGI3NDJmOGYxMjc2YzIxODFlMWQ1In0=
     * user : {"id":9,"phone":"15629693234","nickname":"手机用户15629693234","sex":0,"vip_expiry":0,"user_verify":0,"diamonds":0,"thumb":"storage/userIcon/2020-01-28/ab1f95a9729a7c92cb0627c598cf05cb.jpg"}
     */

    private String token;
    private UserBean user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * id : 9
         * phone : 15629693234
         * nickname : 手机用户15629693234
         * sex : 0
         * vip_expiry : 0
         * user_verify : 0
         * diamonds : 0
         * thumb : storage/userIcon/2020-01-28/ab1f95a9729a7c92cb0627c598cf05cb.jpg
         */

        private int id;
        private String phone;
        private String nickname;
        private int sex;
        private int vip_expiry;
        private int user_verify;
        private int diamonds;
        private String thumb;
        private String invitation_code;

        public String getInvitation_code() {
            return invitation_code;
        }

        public void setInvitation_code(String invitation_code) {
            this.invitation_code = invitation_code;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getVip_expiry() {
            return vip_expiry;
        }

        public void setVip_expiry(int vip_expiry) {
            this.vip_expiry = vip_expiry;
        }

        public int getUser_verify() {
            return user_verify;
        }

        public void setUser_verify(int user_verify) {
            this.user_verify = user_verify;
        }

        public int getDiamonds() {
            return diamonds;
        }

        public void setDiamonds(int diamonds) {
            this.diamonds = diamonds;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}
