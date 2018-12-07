package com.tricon.ruleengine.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricon.ruleengine.api.controller.RuleEngineValidationController;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.eaglesoft.Patient;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.utils.Constants;

public class EagleSoftConnectionController {

	static Class<?> clazz = RuleEngineValidationController.class;

}
