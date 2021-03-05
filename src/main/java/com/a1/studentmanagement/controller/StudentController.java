package com.a1.studentmanagement.controller;

import com.a1.studentmanagement.models.Student;
import com.a1.studentmanagement.service.StudentService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class StudentController {

    private Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void upload(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info("Filename: " + file.getName());
        studentService.uploadStudents(file.getInputStream());
    }

    @RequestMapping(value = "/downloadStudents", method = RequestMethod.GET)
    public void downloadStudents(HttpServletResponse response) throws IOException {
        List<Student> students = studentService.getAllStudents();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=students.xls");

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        addTableHeader(sheet);

        int rowNum = 1;
        HSSFRow row;
        for (Student student : students) {
            row = sheet.createRow(rowNum);
            for (int i = 0; i < 5; i++) {
                HSSFCell cell = row.createCell(i);
                if (i == 0) {
                    cell.setCellValue(student.getId());
                } else if (i == 1) {
                    cell.setCellValue(student.getFirstName());
                } else if (i == 2) {
                    cell.setCellValue(student.getLastName());
                } else if (i == 3) {
                    cell.setCellValue(student.getGender());
                } else {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yy HH:mm");
                    cell.setCellValue(student.getDob().format(dateTimeFormatter));
                }
            }
            rowNum++;
        }

        downloadFile(response, wb);
    }

    private void downloadFile(HttpServletResponse response, HSSFWorkbook wb) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        wb.write(outByteStream);
        byte [] outArray = outByteStream.toByteArray();
        response.setContentType("application/ms-excel");
        response.setContentLength(outArray.length);
        response.setHeader("Expires:", "0"); // eliminates browser caching
        response.setHeader("Content-Disposition", "attachment; filename=students.xls");
        OutputStream outStream = response.getOutputStream();
        outStream.write(outArray);
        outStream.flush();
    }

    private void addTableHeader(HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < 5; i++) {
            HSSFCell cell = row.createCell(i);
            if (i == 0) {
                cell.setCellValue("Student ID");
            } else if (i == 1) {
                cell.setCellValue("First Name");
            } else if (i == 2) {
                cell.setCellValue("Last Name");
            } else if (i == 3) {
                cell.setCellValue("Gender");
            } else {
                cell.setCellValue("DOB");
            }
        }
    }

    @RequestMapping(value = "/student", method = RequestMethod.POST)
    public void addStudent(@RequestBody Student student) {
        studentService.addStudent(student);
    }

    @RequestMapping(value = "/students", method = RequestMethod.GET)
    public List<Student> getStudentsByPage(@RequestParam Integer page) {
        return studentService.getStudentsPerPage(page);
    }

    @RequestMapping(value = "/totalPages", method = RequestMethod.GET)
    public int getTotalPages() {
        return studentService.getTotalPages();
    }

}
