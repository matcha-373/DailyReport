package com.techacademy.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import jakarta.transaction.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        if (reportRepository.existsByEmployeeAndReportDate(report.getEmployee(), report.getReportDate())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        report.setDeleteFlg(false);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {
        Report oldReport = findById(report.getId());
        report.setEmployee(oldReport.getEmployee());

        if (reportRepository.existsByEmployeeAndReportDateAndIdNot(report.getEmployee(), report.getReportDate(),report.getId())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        report.setDeleteFlg(oldReport.isDeleteFlg());

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(oldReport.getCreatedAt());
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

}
