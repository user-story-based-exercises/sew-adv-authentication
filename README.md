SEW | ADV | Authentification

## User Story 1
*As a platform hoster, I only want certain users to be able to create, modify, and delete songs, so that there is no misuse amongst users.*

### Acceptance Criteria
- A mechanism to login by providing username and password is available.
- A session token is used to identify the logged in user.
- The operations create, update, and delete can only be successfully performed by a logged in user.
- A logout mechanism is also available.

#### HINT
- Login: POST request to endpoint /login sending form-data including the properties: username and password
- You can also use cookies instead of header token
- You can use this user info within your import.sql: insert into benutzer(id, username, password) values (1, 'hugo', '$2a$10$MkLTGxdivqj427wEbGrwu.Qbx7G.2z.d31xVb1Qe9UCwimEdpqp1a'); -- password
