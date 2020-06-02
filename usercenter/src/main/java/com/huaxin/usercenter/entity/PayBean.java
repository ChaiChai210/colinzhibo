package com.huaxin.usercenter.entity;

import java.util.List;

public class PayBean {


    /**
     * diamonds : 0
     * pay_list : [{"id":1,"title":"支付宝H5","icon":"http://q68stm6zf.bkt.clouddn.com/icon/20200304/0b34721a5627f6dcf575261d2209452e.jpg","charge_rules":[{"id":1,"coin":100,"money":"10","give":0,"tag":1},{"id":2,"coin":200,"money":"20","give":10,"tag":1},{"id":3,"coin":500,"money":"50","give":20,"tag":2},{"id":5,"coin":800,"money":"70","give":70,"tag":1}]},{"id":2,"title":"支付宝","icon":"http://q68stm6zf.bkt.clouddn.com/icon/20200304/0b34721a5627f6dcf575261d2209452e.jpg","charge_rules":[{"id":4,"coin":500,"money":"45","give":50,"tag":0},{"id":5,"coin":800,"money":"70","give":70,"tag":1}]},{"id":3,"title":"fjfjfy","icon":"http://q68stm6zf.bkt.clouddn.com/icon/20200306/eab5a23a5fdc80a960287e1889a17762.png","charge_rules":[{"id":1,"coin":100,"money":"10","give":0,"tag":1},{"id":5,"coin":800,"money":"70","give":70,"tag":1}]},{"id":4,"title":"n e nnt","icon":"http://q68stm6zf.bkt.clouddn.com/icon/20200306/af2a3b3c7e3c8ecd9e3f55388b9ce648.png","charge_rules":[{"id":5,"coin":800,"money":"70","give":70,"tag":1}]}]
     */

    private int diamonds;
    private List<PayListBean> pay_list;

    public int getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(int diamonds) {
        this.diamonds = diamonds;
    }

    public List<PayListBean> getPay_list() {
        return pay_list;
    }

    public void setPay_list(List<PayListBean> pay_list) {
        this.pay_list = pay_list;
    }

    public static class PayListBean {
        /**
         * id : 1
         * title : 支付宝H5
         * icon : http://q68stm6zf.bkt.clouddn.com/icon/20200304/0b34721a5627f6dcf575261d2209452e.jpg
         * charge_rules : [{"id":1,"coin":100,"money":"10","give":0,"tag":1},{"id":2,"coin":200,"money":"20","give":10,"tag":1},{"id":3,"coin":500,"money":"50","give":20,"tag":2},{"id":5,"coin":800,"money":"70","give":70,"tag":1}]
         */

        private int id;
        private String title;
        private String icon;
        private List<ChargeRulesBean> charge_rules;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public List<ChargeRulesBean> getCharge_rules() {
            return charge_rules;
        }

        public void setCharge_rules(List<ChargeRulesBean> charge_rules) {
            this.charge_rules = charge_rules;
        }

        public static class ChargeRulesBean {
            /**
             * id : 1
             * coin : 100
             * money : 10
             * give : 0
             * tag : 1
             */

            private int id;
            private int coin;
            private String money;
            private int give;
            private int tag;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getCoin() {
                return coin;
            }

            public void setCoin(int coin) {
                this.coin = coin;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public int getGive() {
                return give;
            }

            public void setGive(int give) {
                this.give = give;
            }

            public int getTag() {
                return tag;
            }

            public void setTag(int tag) {
                this.tag = tag;
            }
        }
    }
}
