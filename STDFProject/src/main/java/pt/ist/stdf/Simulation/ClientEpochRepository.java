package pt.ist.stdf.Simulation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientEpochRepository extends JpaRepository<ClientEpoch, ClientEpochId>{

	ClientEpoch findByPrimaryKey(ClientEpochId id);

}
