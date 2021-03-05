package com.a1.studentmanagement.service;

import com.a1.studentmanagement.models.Student;
import com.a1.studentmanagement.repositories.StudentPagingAndSortingRepository;
import com.a1.studentmanagement.repositories.StudentRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private Logger logger = LoggerFactory.getLogger(StudentService.class);

    private static int SHEET_PAGE = 0;

    private final StudentRepository studentRepository;
    private final StudentPagingAndSortingRepository pagingAndSortingRepository;

    public StudentService(StudentRepository studentRepository, StudentPagingAndSortingRepository pagingAndSortingRepository) {
        this.studentRepository = studentRepository;
        this.pagingAndSortingRepository = pagingAndSortingRepository;
    }

    public void uploadStudents(InputStream inputStream) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = wb.getSheetAt(SHEET_PAGE);
        int studentsInserted = 0;

        DataFormatter formatter = new DataFormatter();
        int rows = 0;
        for(Row row : sheet) {
            if (rows == 0) {
                rows++;
                continue;
            }
            int cnt = 0;
            Student student = new Student();
            for(Cell cell : row) {
                String cellValue = formatter.formatCellValue(cell);
                if (cnt == 0) {
                    student.setId(cellValue);
                } else if (cnt == 1) {
                    student.setFirstName(cellValue);
                } else if (cnt == 2) {
                    student.setLastName(cellValue);
                } else if (cnt == 3){
                    student.setGender(cellValue);
                } else {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yy HH:mm");
                    LocalDateTime date = LocalDateTime.parse(cellValue, dateTimeFormatter);
                    if (date.compareTo(LocalDateTime.now()) > 0) {
                        date = date.minusYears(100);
                    }
                    student.setDob(date);
                }
                cnt++;
            }
            studentRepository.save(student);
            studentsInserted++;
        }
        logger.info("Students inserted count: " + studentsInserted);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {
        student.setId(UUID.randomUUID().toString());
        studentRepository.save(student);
    }

    public List<Student> getStudentsPerPage(int page) {
        return pagingAndSortingRepository.findAll(PageRequest.of(page, 10)).toList();
    }

    public int getTotalPages() {
        return pagingAndSortingRepository.findAll(PageRequest.of(0, 10)).getTotalPages();
    }
}






