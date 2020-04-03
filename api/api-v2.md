# API v2 Planning

## Authentication endpoint
- Path `/api/v2/auth/**`  
- Description: (Everything regarding authentication)
    * Authenticating yourself (in return of an auth-token)
    * Getting information about own authentication (like: `isAdmin` or `username`)
    * Changing own authentication information, e.g. changing password, maybe username  
### Sub-Endpoints

#### Authentication Information
- Path `/api/v2/auth`  
- Method `GET`  
- Description: Information about own authentication
- Example Response:  
    ```json
    {
      "admin": true,
      "username": "Administrator",
      "information about access rights": "..."     
    } 
    ```

### Change password
- Path `/api/v2/auth/password`
- Method `POST`
- Description Change password 
- Example Request: 
    ```json
    {
      "password": "new_password_123"
    }
    ```
- Example Response: `204 No Content`

### Authenticate yourself
- Path `/api/v2/auth/login`
- Method `POST`
- Description Authenticate yourself in return of an authentication token
- Example Request:
    ```json
        {
          "username": "administrator",
          "password": "admin123"
        }
    ```

## Classes endpoint
- Path `/api/v2/classes`
- Description: 
    * Getting all accessible classes
    * Adding, modifying and deleting classes
    * Accessing students by class
    * Adding, modifying and deleting students by class
    * Accessing sport results by students by class
    * Adding, modifying and deleting sport results by students by class