package pt.ist.stdf.ServerProgram.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientEpochRepository extends JpaRepository<ClientEpoch, Integer>{

	ClientEpoch findById(ClientEpochId id);

}
