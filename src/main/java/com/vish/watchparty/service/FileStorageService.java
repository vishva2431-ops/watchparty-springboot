package com.vish.watchparty.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET")
        ));
    }

    public String savePoster(MultipartFile file) throws IOException {
        Map upload = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("resource_type", "image")
        );
        return upload.get("secure_url").toString();
    }

    public String saveVideo(MultipartFile file) throws IOException {
        Map upload = cloudinary.uploader().uploadLarge(
                file.getBytes(),
                ObjectUtils.asMap("resource_type", "video")
        );
        return upload.get("secure_url").toString();
    }

    public void delete(String publicId, String resourceType) throws IOException {
        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", resourceType)
        );
    }
}