package org.fd.mcb.modules.journalentry.controller;

import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.dto.request.JournalEntrySearchRequest;
import org.fd.mcb.modules.journalentry.dto.request.ManualJournalEntryRequest;
import org.fd.mcb.modules.journalentry.dto.request.ReversalRequest;
import org.fd.mcb.modules.journalentry.dto.response.JournalEntryDTO;
import org.fd.mcb.modules.journalentry.dto.response.ManualJournalEntryResponse;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.service.JournalEntryQueryService;
import org.fd.mcb.modules.journalentry.service.ManualJournalEntryService;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.response.template.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/journal-entry")
@RequiredArgsConstructor
public class JournalEntryController {

    private final ResponseHelper responseHelper;
    private final ManualJournalEntryService manualJournalEntryService;
    private final JournalEntryQueryService journalEntryQueryService;

    @PostMapping
    public ResponseEntity<ResponseData<ManualJournalEntryResponse>> createManualEntry(
            @Valid @RequestBody ManualJournalEntryRequest request) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                manualJournalEntryService.createManualEntry(request)
        );
    }

    @PostMapping("/{id}/reverse")
    public ResponseEntity<ResponseData<ManualJournalEntryResponse>> reverseEntry(
            @PathVariable Long id,
            @Valid @RequestBody ReversalRequest request) {
        request.setTransactionId(id);
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                manualJournalEntryService.reverseManualEntry(request)
        );
    }

    @GetMapping
    public ResponseEntity<ResponseData<List<JournalEntryDTO>>> searchJournalEntries(
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) EntryType entryType,
            @RequestParam(required = false) ZonedDateTime startDate,
            @RequestParam(required = false) ZonedDateTime endDate) {
        JournalEntrySearchRequest request = new JournalEntrySearchRequest();
        request.setAccountNumber(accountNumber);
        request.setEntryType(entryType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                journalEntryQueryService.searchJournalEntries(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<JournalEntryDTO>> getJournalEntryById(
            @PathVariable Long id) {
        return responseHelper.createResponseData(
                ResponseEnum.SUCCESS,
                journalEntryQueryService.getJournalEntryById(id)
        );
    }
}
