package com.a1.studentmanagement.repositories;

import com.a1.studentmanagement.models.Student;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StudentPagingAndSortingRepository  extends PagingAndSortingRepository<Student, String> {
}
