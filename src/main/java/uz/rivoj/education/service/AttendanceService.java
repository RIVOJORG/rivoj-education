package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.rivoj.education.repository.AttendanceRepository;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;


}
