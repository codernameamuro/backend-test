package kr.co.polycube.backendtest.Domain.user;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PolyUserService {
    private final PolyUserRepository polyUserRepository;

    public List<Long> getAllUsers() {
        return polyUserRepository.findAll()
                .stream()
                .map(PolyUser::getId)
                .collect(Collectors.toList());
    }

    public String getUser(Long id) {
        PolyUser user = polyUserRepository.findById(id).orElse(null);
        if (user == null)
            return null;
        return user.getName();
    }

    public Long postUser(String name) {
        if (name == null || name.equals("null") || name.isEmpty())
            return -1L;
        PolyUser newUser = new PolyUser();
        newUser.setPolyUser(name);
        try {
            polyUserRepository.save(newUser);
            return newUser.getId();
        }
        catch (Exception e) {
            return -1L;
        }
    }

    public Boolean updateUser(Long id, String name) {
        PolyUser user = polyUserRepository.findById(id).orElse(null);
        if (user == null)
            return false;
        user.setPolyUser(name);
        try {
            polyUserRepository.save(user);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
