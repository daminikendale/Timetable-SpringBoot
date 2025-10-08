package com.rspc.timetable;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
// Add this annotation right here. This is the entire fix.
//@AutoConfigureTestDatabase
@ActiveProfiles("dev")
class TimeTableApplicationTests {

	@Test
	void contextLoads() {
	}

}
