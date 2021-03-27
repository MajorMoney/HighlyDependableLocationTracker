The world pandemic we live in calls for tools for dependable location tracking and
contact tracing. The goal of the project is to design and implement a “Highly
Dependable Location Tracker” (HDLT) system with the following characteristics:

  ● Users periodically send their location to a server. To do this they perform the
  following steps:
    ○ A user that wants to prove its location - known as the prover -
      broadcasts a location proof request to other users that might be
      nearby.
    ○ A user that receives a location proof request - known as the witness -
      checks if the prover is close enough and if that is the case sends back
      a reply. The reply encodes the fact that those two users were nearby at
      that time.
    ○ After receiving the proof(s), the user sends it/them to the location
      server.
  ● Time is split into epochs. Users prove their location once per epoch. Even
  though in a realistic scenario users can move during an epoch, for simplicity,
  we assume that correct users do not change their location in the same epoch.
  ● The location server stores, for each user, the sequence of location reports,
  containing, for each epoch, the user’s location and proofs. The server allows
  users to consult the stored data. There is a special user, representing the
  Healthcare Authorities (HA) that can obtain the location of all users across
  any epoch.
  ● The system should be resilient to attackers that aim to tamper with the
  integrity of the locations and proofs. Furthermore, attackers should not be
  able to generate proofs for users other than themselves
