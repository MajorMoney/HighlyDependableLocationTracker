package pt.ist.stdf.ServerProgram.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpochRepository extends JpaRepository<Epoch, Integer>{

	Epoch findById(int id);

}
