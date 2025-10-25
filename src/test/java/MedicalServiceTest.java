import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalServiceTest {
    @Mock
    PatientInfoRepository patientInfoRepository;
    @Mock
    SendAlertService alertService;
    @InjectMocks
    MedicalServiceImpl medicalService;
    PatientInfo patientInfo;

    @BeforeEach
    void setup() {
        patientInfo = new PatientInfo("1", "Иван", "Иванов", LocalDate.of(1985, 3, 25),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));
    }

    @Test
    void checkBloodPressureTest() {
        when(patientInfoRepository.getById("1")).thenReturn(patientInfo);
        medicalService.checkBloodPressure("1", new BloodPressure(130, 90));
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(alertService).send(captor.capture());
        Assertions.assertEquals("Warning, patient with id: 1, need help", captor.getValue());
    }

    @Test
    void checkTemperatureTest() {
        when(patientInfoRepository.getById("1")).thenReturn(patientInfo);
        medicalService.checkTemperature("1", new BigDecimal("35.0"));
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(alertService).send(captor.capture());
        Assertions.assertEquals("Warning, patient with id: 1, need help", captor.getValue());
    }

    @Test
    void notSendTest() {
        when(patientInfoRepository.getById("1")).thenReturn(patientInfo);
        medicalService.checkBloodPressure("1", new BloodPressure(120, 80));
        medicalService.checkTemperature("1", new BigDecimal("36.6"));
        verify(alertService, never()).send(anyString());
    }
}
