package asterisk.sun.booking_tours.application.api.tour;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewDetailRequestDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewDetailResponseDTO;
import asterisk.sun.booking_tours.common.aspect.Loggable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(ApiV1.TOUR_ENDPOINT)
public class ApiTourController {
    private final ApiTourService apiTourService;

    public ApiTourController(ApiTourService apiTourService) {
        this.apiTourService = apiTourService;
    }

    @Loggable
    @GetMapping("/detail")
    public ResponseEntity<SuccessResponse<ViewDetailResponseDTO>> viewDetail(ViewDetailRequestDTO param) {
        ViewDetailResponseDTO responseDTO = apiTourService.getTourDetail(param.getId());

        SuccessResponse<ViewDetailResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Get Tour Detail Successfully", responseDTO);

        return ResponseEntity.ok(response);
    }

}
