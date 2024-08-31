package kr.co.polycube.backendtest.Domain.user;

import kr.co.polycube.backendtest.Domain.user.Dto.postUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PolyUserController {

    private final PolyUserService polyUserService;

    /***
    * Description :This method is getting all users
    * Writer : Jeong eun seong
    * Update Date : 2024-08-30
    * Method : GET
    * Path : /users
    * Request : None
    * Response : List<Long>
    *     Response : [1, 2, 3, ...]
    ***/
    @GetMapping("/users")
    public List<Long> getAllUsers() {
        return polyUserService.getAllUsers();
    }



    /***
     * Description :This method is posting user
     * Writer : Jeong eun seong
     * Update Date : 2024-08-30
     * Method : POST
     * Path : /users
     * Request : postUserDto
     * Response : Map<String, String>
     *      SUCCESS
`    *          Response : key = id, value = {id} (Long)
     *          Response : key = result, value = success
     *      FAIL
     *          Response : key = result, value = fail / key = reason, value = name is null or empty
     *          Response : key = result, value = fail / key = reason, value = {exception message}
     *     ***/
    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> postUser(@RequestBody postUserDto postUserDto) {
        Map<String, String> response = new HashMap<>();
        Long newUserId = polyUserService.postUser(postUserDto.getName());
        try{
            if (newUserId != -1L) {
                response.put("id", newUserId.toString());
                response.put("result", "success");
                return ResponseEntity.ok(response);
            }
            else {
                response.put("result", "fail");
                response.put("reason", "name is null or empty");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e){
            response.put("result", "fail");
            response.put("reason", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    /***
     * Description :This method is getting user
     * Writer : Jeong eun seong
     * Update Date : 2024-08-30
     * Method : GET
     * Path : /users/{id}
     * Request : Long
     * Response : Map<String, String>
     *      SUCCESS
     *          Response : key = id, value = {id} (Long)
     *          Response : key = name, value = {name} (String)
     *          Response : key = result, value = success
     *      FAIL
     *          Response : key = result, value = fail / key = reason, value = invalid id
     *          Response : key = result, value = fail / key = reason, value = user not found
     *          Response : key = result, value = fail / key = reason, value = {exception message}
     *     ***/
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> getUser(@PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();
        if (id <= 0) {
            response.put("result", "fail");
            response.put("reason", "invalid id");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            String name = polyUserService.getUser(id);

            if (name != null) {
                response.put("id", id.toString());
                response.put("name", name);
                return ResponseEntity.ok(response);
            } else {
                response.put("result", "fail");
                response.put("reason", "user not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("result", "fail");
            response.put("reason", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /***
     * Description :This method is updating user
     * Writer : Jeong eun seong
     * Update Date : 2024-08-31
     * Method : PUT
     * Path : /users/{id}
     * Request : Long, postUserDto
     * Response : Map<String, String>
     *      SUCCESS
     *          Response : key = id, value = {id} (Long)
     *          Response : key = name, value = {name} (String)
     *          Response : key = result, value = success
     *      FAIL
     *          Response : key = result, value = fail / key = reason, value = invalid id
     *          Response : key = result, value = fail / key = reason, value = user not found
     *          Response : key = result, value = fail / key = reason, value = {exception message}
     *     ***/
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable("id") Long id, @RequestBody postUserDto postUserDto) {
        Map<String, String> response = new HashMap<>();
        if (id <= 0) {
            response.put("result", "fail");
            response.put("reason", "invalid id");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            if (polyUserService.updateUser(id, postUserDto.getName())) {
                response.put("result", "success");
                response.put("id", id.toString());
                response.put("name", postUserDto.getName());
                return ResponseEntity.ok(response);
            } else {
                response.put("result", "fail");
                response.put("reason", "user not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("result", "fail");
            response.put("reason", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
