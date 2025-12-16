package com.najmi.fleetshare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:C:/fleetshare-uploads}")
    private String uploadDir;

    @Value("${app.upload.vehicle-images:C:/fleetshare-uploads/vehicles}")
    private String vehicleImagesDir;

    @Value("${app.upload.profile-images:C:/fleetshare-uploads/profiles}")
    private String profileImagesDir;

    @Value("${app.upload.payment-proofs:C:/fleetshare-uploads/payments}")
    private String paymentProofsDir;

    /**
     * Initialize upload directories on application startup
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(vehicleImagesDir));
            Files.createDirectories(Paths.get(profileImagesDir));
            Files.createDirectories(Paths.get(paymentProofsDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    /**
     * Store a vehicle image
     * 
     * @param file      The uploaded file
     * @param vehicleId The vehicle ID for naming
     * @return The relative URL path to access the image
     */
    public String storeVehicleImage(MultipartFile file, Long vehicleId) throws IOException {
        String filename = generateFilename("vehicle", vehicleId, getFileExtension(file.getOriginalFilename()));
        Path targetPath = Paths.get(vehicleImagesDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/vehicles/" + filename;
    }

    /**
     * Store a profile image
     * 
     * @param file   The uploaded file
     * @param userId The user ID for naming
     * @return The relative URL path to access the image
     */
    public String storeProfileImage(MultipartFile file, Long userId) throws IOException {
        String filename = generateFilename("profile", userId, getFileExtension(file.getOriginalFilename()));
        Path targetPath = Paths.get(profileImagesDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/profiles/" + filename;
    }

    /**
     * Store a payment proof document
     * 
     * @param file      The uploaded file
     * @param bookingId The booking ID for naming
     * @return The relative URL path to access the document
     */
    public String storePaymentProof(MultipartFile file, Long bookingId) throws IOException {
        String filename = generateFilename("payment", bookingId, getFileExtension(file.getOriginalFilename()));
        Path targetPath = Paths.get(paymentProofsDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/payments/" + filename;
    }

    /**
     * Delete a file by its URL path
     * 
     * @param fileUrl The URL path of the file to delete
     * @return true if deleted successfully
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return false;
        }
        try {
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir).resolve(relativePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Generate a unique filename with timestamp and UUID
     */
    private String generateFilename(String prefix, Long id, String extension) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        long timestamp = System.currentTimeMillis();
        return String.format("%s-%d-%d-%s%s", prefix, id, timestamp, uniqueId, extension);
    }

    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * Validate if file is an allowed image type
     */
    public boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp"));
    }

    /**
     * Validate if file is an allowed document type (for payment proofs)
     */
    public boolean isValidDocumentFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("application/pdf"));
    }
}
