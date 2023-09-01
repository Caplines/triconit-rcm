package com.tricon.rcm.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tricon.rcm.db.entity.RcmAttachmentType;
import com.tricon.rcm.db.entity.RcmClaimAttachment;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimAttachmentsResponseDto;
import com.tricon.rcm.dto.FileResponseDto;
import com.tricon.rcm.dto.RcmClaimDeAttachmentDto;
import com.tricon.rcm.dto.customquery.RcmClaimAttachmentDto;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmAttachmentTypeRepo;
import com.tricon.rcm.jpa.repository.RcmClaimAttachmentRepo;
import com.tricon.rcm.jpa.repository.RcmClaimRepository;
import com.tricon.rcm.jpa.repository.RcmTeamRepo;
import com.tricon.rcm.security.JwtUser;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.MessageConstants;

@Service
public class AttachmentServiceImpl {

	@Value("${app.attachment.dir.path}")
	private String attachmentDirPath;

	@Autowired
	RcmClaimAttachmentRepo attachmentRepo;

	@Autowired
	RcmAttachmentTypeRepo attachmentTypeRepo;

	@Autowired
	RcmClaimRepository claimRepo;

	@Autowired
	RcmTeamRepo teamRepo;

	@Autowired
	RcmUtilServiceImpl utilService;

	@Autowired
	RCMUserRepository userRepo;

	private final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

	@Transactional(rollbackOn = Exception.class)
	public FileResponseDto saveClaimAttachment(MultipartFile file, String claimUuid, JwtUser jwtUser, int createdByteam,
			int attachmentTypeId) throws Exception {
		FileResponseDto response = null;
		InputStream in = null;
		String fullPathName = utilService.getFileAbsolutePath(attachmentDirPath);
		RcmClaimAttachment claimAttachment = null;
		Optional<RcmAttachmentType> attachmentType = null;
		RcmUser loginUser = null;
		RcmClaims rcmClaims = null;
		String claimAttachmentFolder = null;
		String fileName = null;
		RcmTeam team = null;
		int fileCounts=0;
		if (fullPathName != null) {
			try {
				rcmClaims = claimRepo.findByClaimUuid(claimUuid);
				if (rcmClaims == null)
					return FileResponseDto.builder().msg(MessageConstants.CLAIM_NOT_EXIST).fileResponseStatus(false)
							.build();
				in = file.getInputStream();
				attachmentType = attachmentTypeRepo.findById(attachmentTypeId);
				if (attachmentType.isPresent() || (file.isEmpty() && attachmentTypeId == 0)) {
					
					
					loginUser = userRepo.findByEmail(jwtUser.getUsername());
					team = new RcmTeam();
					claimAttachment = new RcmClaimAttachment();
					team.setId(createdByteam);
					claimAttachment.setCreatedBy(loginUser);
					claimAttachment.setCreatedByteam(team);
					if (!file.isEmpty()) {
						fileName = Integer.toString(attachmentTypeId).concat(Constants.HYPHEN)
								.concat(file.getOriginalFilename());
						fileCounts=attachmentRepo.fileCount(fileName ,claimUuid);
					}
					claimAttachment.setFileName(fileName);
					claimAttachment.setFileLocation(fullPathName);
					claimAttachment.setStatus(true);
					claimAttachment.setUpdatedBy(loginUser);
					claimAttachment.setCreatedDate(Timestamp.from(Instant.now()));
					claimAttachment.setAtchType(attachmentType.orElse(null));
					claimAttachment.setUuid(rcmClaims);
					if(fileCounts<1){
						attachmentRepo.save(claimAttachment);
						rcmClaims.setAttachmentCount(rcmClaims.getAttachmentCount()+1);
						claimRepo.save(rcmClaims);
					}
					if (!file.isEmpty() && fileCounts<1) {
						// Will Make folder with the help of claimUuid to save each file separatlty

						claimAttachmentFolder = utilService
								.getFileAbsolutePath(attachmentDirPath.concat(File.separator).concat(claimUuid));

						Files.copy(in, Paths.get(claimAttachmentFolder.concat(File.separator).concat(fileName)),
								StandardCopyOption.REPLACE_EXISTING);	
					}
						response = FileResponseDto.builder().msg(MessageConstants.DATA_SAVED).fileResponseStatus(true)
								.build();
					
				} else
					response = FileResponseDto.builder().msg(MessageConstants.ATTACHMENT_TYPE_NOT_EXIST)
							.fileResponseStatus(false).build();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				response = FileResponseDto.builder().msg(null).fileResponseStatus(false).build();
			} finally {
				in.close();
			}
		}
		return response;
	}

	public List<ClaimAttachmentsResponseDto> getClaimAttachments(String claimUuid) throws Exception {
		ClaimAttachmentsResponseDto attachmentDto = null;
		List<ClaimAttachmentsResponseDto> responseData = new ArrayList<>();
		List<RcmClaimAttachmentDto> data = attachmentRepo.findByAttachmentId(claimUuid);
		if (!data.isEmpty()) {
			for (RcmClaimAttachmentDto d : data) {
				ClaimAttachmentsResponseDto.File inner=new ClaimAttachmentsResponseDto().new File();
				inner.setName((d.getFileName()==null ||d.getFileName().isEmpty())?"":d.getFileName().split(Constants.HYPHEN,2)[1]);
				attachmentDto = ClaimAttachmentsResponseDto.builder().id(d.getId())
						.file(inner).attachmentId(d.getAttachmentId())
						.isDeleted(d.getIsDeleted()).build();
				responseData.add(attachmentDto);
			}
		}
		return responseData;
	}

	@Transactional(rollbackOn = Exception.class)
	public FileResponseDto removeClaimAttachment(RcmClaimDeAttachmentDto dto, JwtUser jwtUser) throws Exception {
		FileResponseDto response = null;
		File existingFile = null;
		File renameFile = null;
		String renameFileName = null;
		RcmUser loginUser = null;
		RcmClaims claims=null;
		int status = 0;
		List<RcmClaimAttachmentDto> data = attachmentRepo.findByAttachmentsById(dto.getClaimAttachmentId(),
				dto.getClaimUuid());
		if (!data.isEmpty()) {
			for (RcmClaimAttachmentDto d : data) {
				String checkEmptyFileName=d.getFileName();
				if(checkEmptyFileName==null ||checkEmptyFileName.isEmpty()) {
					response = FileResponseDto.builder().msg("Empty FileName").fileResponseStatus(false)
							.build();
					return response;
				}
				existingFile = new File(attachmentDirPath.concat(File.separator).concat(dto.getClaimUuid())
						.concat(File.separator).concat(checkEmptyFileName));
				if (existingFile.exists()) {
					renameFileName = Constants.REMOVE_ATTACHMENT_PREFIX.concat(checkEmptyFileName);
					renameFile = new File(attachmentDirPath.concat(File.separator).concat(dto.getClaimUuid())
							.concat(File.separator).concat(renameFileName));
					loginUser = userRepo.findByEmail(jwtUser.getUsername());
					// find existing rename(remove) file from db

					RcmClaimAttachmentDto renameFileData = attachmentRepo.findRenameFile(checkEmptyFileName,
							d.getClaimUuid());
					if (renameFileData == null) {
						status = attachmentRepo.updateAttachmentStatusById(loginUser, d.getId(), renameFileName);
						if (status > 0) {
							existingFile.renameTo(renameFile);
							claims = claimRepo.findByClaimUuid(d.getClaimUuid());
							if(claims!=null) {
							claims.setAttachmentCount(claims.getAttachmentCount()==0?0:claims.getAttachmentCount()-1);
							claimRepo.save(claims);}else logger.error("Claim Does't Exist");
							response = FileResponseDto.builder().msg(MessageConstants.RECORDS_UPDATE)
									.fileResponseStatus(true).build();
						}
					} else {

						String reName = Constants.REMOVE_ATTACHMENT_PREFIX.concat(renameFileData.getRenameFile());
						status = attachmentRepo.updateAttachmentStatusById(loginUser, d.getId(), reName);
						if (status > 0) {
							File replaceFile = new File(attachmentDirPath.concat(File.separator).concat(dto.getClaimUuid())
									.concat(File.separator).concat(reName));
							existingFile.renameTo(replaceFile);
							if(claims!=null) {
							claims = claimRepo.findByClaimUuid(d.getClaimUuid());
							claims.setAttachmentCount(claims.getAttachmentCount()==0?0:claims.getAttachmentCount()-1);
							claimRepo.save(claims);}else logger.error("Claim Does't Exist");
							response = FileResponseDto.builder().msg(MessageConstants.RECORDS_UPDATE)
									.fileResponseStatus(true).build();
						}
					}

					if (status <= 0) {
						response = FileResponseDto.builder().msg(MessageConstants.UPDATION_FAIL)
								.fileResponseStatus(false).build();
					}
				} else
					response = FileResponseDto.builder().msg(MessageConstants.FILE_NOT_EXIST).fileResponseStatus(false)
							.build();
			   }
		     } else
			       response = FileResponseDto.builder().msg(MessageConstants.RECORD_NOT_EXIST).fileResponseStatus(false)
					.build();

		return response;
	}

	public Object[] getAttachmentFile(String id) throws Exception {
		RcmClaimAttachmentDto data = attachmentRepo.findAttachmentFile(id);
		String fullPath = null;
		Object obj[] = new Object[2];
		if (data != null) {
			fullPath = data.getFileLocation().concat(File.separator).concat(data.getClaimUuid()).concat(File.separator)
					.concat(data.getFileName());
			obj[0] = fullPath;
			obj[1] = data.getFileName();
			return obj;
		}
		return obj;
	}

	public int getAttachmentCount(String claimUuid) throws Exception {
		return attachmentRepo.attachmentCount(claimUuid);
	}

}
