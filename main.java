package com.cams.core.rulesui.api;

import com.cams.core.rulesui.api.dto.DrlFileResponse;
import com.cams.core.rulesui.config.ScmProperties;
import com.cams.core.rulesui.drl.DrlService;
import com.cams.core.rulesui.scm.dto.ScmFile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drl")
@CrossOrigin(origins = "*")
public class DrlController {

    private final DrlService drlService;
    private final ScmProperties props;

    public DrlController(DrlService drlService, ScmProperties props) {
        this.drlService = drlService;
        this.props = props;
    }

    @GetMapping("/files")
    public List<String> listFiles() {
        return drlService.listDrlFiles();
    }

    @GetMapping("/file")
    public DrlFileResponse getFile(@RequestParam("name") String fileName) {
        ScmFile f = drlService.getDrlFileByName(fileName);
        return new DrlFileResponse(
            props.getProject(),
            props.getDefaultBranch(),
            f.path(),
            f.revision(),
            f.content()
        );
    }
}
