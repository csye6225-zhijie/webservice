package com.csye6225.webservice.Model.VO;

import com.csye6225.webservice.Model.Image;

public class ImageVO {
    private String id;
    private String user_id;
    private String file_name;
    private String upload_date;
    private String url;

    public ImageVO(Image image){
        this.id = image.getId();
        this.file_name = image.getFile_name();
        this.user_id = image.getUser_id();
        this.upload_date = image.getUpload_date();
        this.url = image.getUrl();
    }

    public ImageVO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }
}
