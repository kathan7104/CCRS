package com.example.demo.repository;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourseAndStatus(User student, Course course, Enrollment.EnrollmentStatus status);
    List<Enrollment> findByCourse(Course course);
    long countByCourseAndStatus(Course course, Enrollment.EnrollmentStatus status);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByStudentIdAndStatus(Long studentId, Enrollment.EnrollmentStatus status);
    List<Enrollment> findByStudentIdAndStatusOrderByRegisteredAtDesc(Long studentId, Enrollment.EnrollmentStatus status);
    List<Enrollment> findByStatusOrderByRegisteredAtDesc(Enrollment.EnrollmentStatus status);
    List<Enrollment> findByStatusInOrderByRegisteredAtDesc(List<Enrollment.EnrollmentStatus> statuses);

    @Query("""
            select distinct e
            from Enrollment e
            join fetch e.student s
            join fetch e.course c
            where e.status = com.example.demo.entity.Enrollment$EnrollmentStatus.ENROLLED
              and c.id in :courseIds
              and lower(c.department) = lower(:department)
              and exists (
                    select 1
                    from InvoiceItem ii
                    join ii.invoice i
                    where ii.course = c
                      and i.student = s
                      and i.status = com.example.demo.entity.Invoice$InvoiceStatus.PAID
              )
            order by c.code asc, s.fullName asc
            """)
    List<Enrollment> findEnrolledAndPaidByCourseIdsAndDepartment(@Param("courseIds") Set<Long> courseIds,
                                                                 @Param("department") String department);

    @Query("""
            select distinct e
            from Enrollment e
            join fetch e.student s
            join fetch e.course c
            where e.status = com.example.demo.entity.Enrollment$EnrollmentStatus.ENROLLED
              and lower(c.department) = lower(:department)
              and exists (
                    select 1
                    from InvoiceItem ii
                    join ii.invoice i
                    where ii.course = c
                      and i.student = s
                      and i.status = com.example.demo.entity.Invoice$InvoiceStatus.PAID
              )
            order by c.code asc, s.fullName asc
            """)
    List<Enrollment> findEnrolledAndPaidByDepartment(@Param("department") String department);

    @Query("""
            select distinct e
            from Enrollment e
            join fetch e.student s
            join fetch e.course c
            where lower(c.department) = lower(:department)
              and e.status in (
                com.example.demo.entity.Enrollment$EnrollmentStatus.APPROVED,
                com.example.demo.entity.Enrollment$EnrollmentStatus.ENROLLED
              )
            order by c.programName asc, c.code asc, s.fullName asc
            """)
    List<Enrollment> findApprovedOrEnrolledByDepartment(@Param("department") String department);
}
