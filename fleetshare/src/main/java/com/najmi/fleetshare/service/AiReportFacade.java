package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.AiQueryRequest;
import com.najmi.fleetshare.dto.AiQueryResponse;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiReportFacade {

    @Autowired
    private AiAssistantService aiAssistantService;

    @Autowired
    private ReportService reportService;

    /**
     * Process an AI query with authentication.
     *
     * @param request      the query request
     * @param session      HTTP session (for auth and cache)
     * @param requireAdmin true for admin endpoints, false for owner endpoints
     * @return the AI query response
     */
    public AiQueryResponse handleQuery(AiQueryRequest request, HttpSession session, boolean requireAdmin) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null) {
            return AiQueryResponse.error("Unauthorized");
        }
        if (requireAdmin && user.getAdminDetails() == null) {
            return AiQueryResponse.error("Unauthorized");
        }
        if (!requireAdmin && user.getOwnerDetails() == null) {
            return AiQueryResponse.error("Unauthorized");
        }

        Long ownerId = requireAdmin ? user.getUserId() : user.getOwnerDetails().getFleetOwnerId();

        try {
            return aiAssistantService.processQuery(
                    request.getQuery(), ownerId, requireAdmin, request.getProvider(), session);
        } catch (Exception e) {
            return AiQueryResponse.error("AI service error: " + e.getMessage());
        }
    }

    /**
     * Process an AI query and return downloadable content.
     *
     * @param request      the query request
     * @param format       "pdf" or "csv"
     * @param session      HTTP session (for auth and cache)
     * @param requireAdmin true for admin endpoints, false for owner endpoints
     * @return download result, or null if unauthorized or failed
     */
    public DownloadResult handleDownload(AiQueryRequest request, String format, HttpSession session, boolean requireAdmin) {
        AiQueryResponse aiResponse = handleQuery(request, session, requireAdmin);
        if (!aiResponse.isSuccess()) {
            return null;
        }

        try {
            byte[] content;
            String contentType;
            String filename;

            List<String> remarks = request.getRemarks();

            if ("csv".equalsIgnoreCase(format)) {
                content = reportService.generateAiCsvReport(aiResponse, remarks);
                contentType = "text/csv";
                filename = "ai-report.csv";
            } else {
                content = reportService.generateAiPdfReport(aiResponse, request.getQuery(), remarks);
                contentType = "application/pdf";
                filename = "ai-report.pdf";
            }

            return new DownloadResult(content, contentType, filename);
        } catch (Exception e) {
            return null;
        }
    }

    public static class DownloadResult {
        public final byte[] content;
        public final String contentType;
        public final String filename;

        public DownloadResult(byte[] content, String contentType, String filename) {
            this.content = content;
            this.contentType = contentType;
            this.filename = filename;
        }
    }
}
