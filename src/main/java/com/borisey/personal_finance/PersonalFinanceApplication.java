package com.borisey.personal_finance;

import com.borisey.personal_finance.models.PersonType;
import com.borisey.personal_finance.models.Type;
import com.borisey.personal_finance.repo.PersonTypeRepository;
import com.borisey.personal_finance.repo.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalFinanceApplication implements CommandLineRunner {

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private PersonTypeRepository personTypeRepository;

	public static void main(String[] args) {
		SpringApplication.run(PersonalFinanceApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Type type1 = new Type();
		type1.setId(1L);
		type1.setTitle("Доход");
		typeRepository.save(type1);

		Type type2 = new Type();
		type2.setId(2L);
		type2.setTitle("Расход");
		typeRepository.save(type2);

		PersonType personType1 = new PersonType();
		personType1.setId(1L);
		personType1.setTitle("Физическое лицо");
		personTypeRepository.save(personType1);

		PersonType personType2 = new PersonType();
		personType2.setId(2L);
		personType2.setTitle("Юридическое лицо");
		personTypeRepository.save(personType2);
	}
}
