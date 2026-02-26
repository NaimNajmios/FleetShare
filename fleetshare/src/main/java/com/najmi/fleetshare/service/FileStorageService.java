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

    @Value("${app.upload.qr-codes:C:/fleetshare-uploads/qrcodes}")
    private String qrCodesDir;

    /**
     * Initialize upload directories on application startup
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(vehicleImagesDir));
            Files.createDirectories(Paths.get(profileImagesDir));
            Files.createDirectories(Paths.get(paymentProofsDir));
            Files.createDirectories(Paths.get(qrCodesDir));
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
        validateImageFile(file);
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
        validateImageFile(file);
        String filename = generateFilename("profile", userId, getFileExtension(file.getOriginalFilename()));
        Path targetPath = Paths.get(profileImagesDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/profiles/" + filename;
    }

    /**
     * Store a QR code image
     * 
     * @param file    The uploaded QR code image file
     * @param ownerId The fleet owner ID for naming
     * @return The relative URL path to access the image
     */
    public String storeQrCodeImage(MultipartFile file, Long ownerId) throws IOException {
        validateImageFile(file);
        String filename = generateFilename("qr", ownerId, getFileExtension(file.getOriginalFilename()));
        Path targetPath = Paths.get(qrCodesDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/qrcodes/" + filename;
    }

    /**
     * Store a payment proof document
     * 
     * @param file      The uploaded file
     * @param bookingId The booking ID for naming
     * @return The relative URL path to access the document
     */
    public String storePaymentProof(MultipartFile file, Long bookingId) throws IOException {
        // Payment proofs can be images or PDFs
        if (!isValidDocumentFile(file)) {
            throw new IllegalArgumentException("Invalid document type. Allowed: PDF, JPG, PNG");
        }
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
     * Extract file extension from filename and normalize
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // Default to safe extension if none provided
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    /**
     * Validate if file is an allowed image type
     */
    public boolean isValidImageFile(MultipartFile file) {
        try {
            validateImageFile(file);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates image file content using magic bytes and extension
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedImageExtension(extension)) {
            throw new IllegalArgumentException("Invalid file extension. Allowed: .jpg, .jpeg, .png, .gif, .webp");
        }

        if (!hasValidImageSignature(file)) {
            throw new IllegalArgumentException("Invalid file content. Not a valid image.");
        }
    }

    private boolean isAllowedImageExtension(String extension) {
        return extension.equals(".jpg") || extension.equals(".jpeg") ||
                extension.equals(".png") || extension.equals(".gif") ||
                extension.equals(".webp");
    }

    private boolean hasValidImageSignature(MultipartFile file) throws IOException {
        try (java.io.InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            if (is.read(header) < 4)
                return false;

            // JPEG: FF D8 FF
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF)
                return true;
            // PNG: 89 50 4E 47
            if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E
                    && header[3] == (byte) 0x47)
                return true;
            // GIF: 47 49 46 38
            if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46
                    && header[3] == (byte) 0x38)
                return true;
            // WEBP (RIFF...WEBP)
            if (header[0] == (byte) 0x52 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46
                    && header[3] == (byte) 0x46)
                return true;

            return false;
        }
    }

    /**
     * Validate if file is an allowed document type (for payment proofs)
     */
    public boolean isValidDocumentFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return false;
            }

            // Check extension
            String extension = getFileExtension(file.getOriginalFilename());
            boolean isImage = isAllowedImageExtension(extension);
            boolean isPdf = extension.equals(".pdf");

            if (!isImage && !isPdf) {
                return false;
            }

            // Check magic bytes
            if (isImage) {
                return hasValidImageSignature(file);
            } else {
                return hasValidPdfSignature(file);
            }
        } catch (IOException e) {
            return false;
        }
    }

    private boolean hasValidPdfSignature(MultipartFile file) throws IOException {
        try (java.io.InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            if (is.read(header) < 4)
                return false;

            // PDF: 25 50 44 46 (%PDF)
            return header[0] == (byte) 0x25 && header[1] == (byte) 0x50 &&
                    header[2] == (byte) 0x44 && header[3] == (byte) 0x46;
        }
    }
}
