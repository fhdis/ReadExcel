package main.com.administrator.excel2;

public class ExcelBean {
    private String name;
    private String tel;
    public ExcelBean(String name,String tel){
        this.name = name;
        this.tel = tel;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
