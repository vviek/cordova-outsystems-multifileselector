package org.multiplefileselector;

public class ImageModel {

    private String image;
    private String fileName;
    private float fileSize;
    private String base64ImageData;

    public ImageModel(String image, String base64ImageData,String fileName,float fileSize) {
        this.image = image;
        this.base64ImageData = base64ImageData;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getImage() {
        return image;
    }
    public String getBase64ImageData() {
        return base64ImageData;
    }

    public String getFileName() {
        return fileName;
    }



    public float getFileSize() {
        return fileSize;
    }

}