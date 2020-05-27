package org.multiplefileselector;

public class ImageModel {


    private String fileName;
    private float fileSize;
    private String base64ImageData;

    public ImageModel( String base64ImageData,String fileName,float fileSize) {

        this.base64ImageData = base64ImageData;
        this.fileName = fileName;
        this.fileSize = fileSize;
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