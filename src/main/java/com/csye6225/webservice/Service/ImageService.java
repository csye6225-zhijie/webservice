package com.csye6225.webservice.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.csye6225.webservice.DAO.ImageDaoImpl;
import com.csye6225.webservice.Model.Image;
import com.csye6225.webservice.Model.VO.ImageVO;
import com.csye6225.webservice.Model.VO.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@org.springframework.stereotype.Service("imageService")
@Transactional(readOnly=true)
public class ImageService {
    private AmazonS3 amazonS3;
    private Date date;
    @Value("{bucket_name}")
    public String bucketName;

    @Autowired
    private ImageDaoImpl imageDao;

    @Transactional
    public void save(Image image){
        imageDao.save(image);
    }

    public void delete(String id){ imageDao.delete(id);}

    public Image findByUserId(String user_id){
       Image image = imageDao.findByUserId(user_id);
       return image;
    }

    public ImageVO saveFile(UserVO userVO, InputStream is) throws Exception {
        Image image = new Image();
        image.setFile_name("user-image.jpeg");

        String url = bucketName + "/" + image.getUser_id() + "/" + image.getFile_name();
        image.setUrl(url);
        date = new Date();
        image.setUser_id(userVO.getId());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String currentTime = sdf.format(date);
        image.setUpload_date(currentTime);

        uploadImageToS3(is, image.getFile_name());

        save(image);  // save image Object into DB
        ImageVO imageVO = new ImageVO(image);
        return imageVO;
    }

    public void deleteFile(Image image) {
        //Delete image Object from S3
        amazonS3.deleteObject(bucketName, image.getFile_name());
        //Delete image record from DB
        delete(image.getId());
    }

    private void uploadImageToS3(InputStream is, String file_name) throws Exception {
        String fileName = "temp";
        File tempFile = null;
        try {
            tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
            java.nio.file.Files.copy(
                    is,
                    tempFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            amazonS3.putObject(bucketName, file_name, tempFile);

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error occurs when transferring the file to tmp file ", e);
        } finally {
            if (null != tempFile) {
                tempFile.deleteOnExit();
            }
        }
    }

}
