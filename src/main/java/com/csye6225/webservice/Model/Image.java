package com.csye6225.webservice.Model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="image")
@GenericGenerator(name = "system-uuid",strategy = "uuid.hex")
public class Image {
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    private String id;

    @Column(name = "file_name")
    private String file_name;

    @Column(name = "url")
    private String url;

    @Column(name = "upload_date")
    private String upload_date;

    @Column(name = "user_id")
    private String user_id;

    public Image() {
    }

    public Image(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getUrl() {
        return url;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }
}
