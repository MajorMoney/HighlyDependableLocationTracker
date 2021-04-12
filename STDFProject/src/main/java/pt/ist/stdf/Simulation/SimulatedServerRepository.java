package pt.ist.stdf.Simulation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulatedServerRepository extends JpaRepository<SimulatedServer, Integer>{

	SimulatedServer findById(int id);

}
