package pt.ist.stdf.HighlyDependableTracker.data;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ist.stdf.HighlyDependableTracker.Model.SimulatedServer;

@org.springframework.stereotype.Repository
public interface SimulatedServerRepository extends JpaRepository<SimulatedServer, Integer>{


}
