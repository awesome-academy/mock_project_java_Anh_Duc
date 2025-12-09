package asterisk.sun.booking_tours.application.api.tour;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.application.api.tour.dto.ListToursResponseDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.SearchToursRequestDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewDetailRequestDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewDetailResponseDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.tour.Tour;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(ApiV1.TOUR_ENDPOINT)
public class ApiTourController {
    private final ApiTourService apiTourService;

    public ApiTourController(ApiTourService apiTourService) {
        this.apiTourService = apiTourService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ListToursResponseDTO>> getListTours(SearchToursRequestDTO param) {
        Page<Tour> tours = apiTourService.getListTours(param);
        List<ListToursResponseDTO> data = MapperHelper.mapList(tours.getContent(), ListToursResponseDTO.class);

        PaginatedResponse<ListToursResponseDTO> response = new PaginatedResponse<>(
                HttpStatus.OK.value(),
                "Get List Tours Successfully",
                data,
                tours.getTotalElements(),
                tours.getNumber() + 1,
                tours.getSize()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<SuccessResponse<ViewDetailResponseDTO>> viewDetail(ViewDetailRequestDTO param) {
        ViewDetailResponseDTO responseDTO = apiTourService.getTourDetail(param.getId());

        SuccessResponse<ViewDetailResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Get Tour Detail Successfully", responseDTO);

        return ResponseEntity.ok(response);
    }

}
