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
	public ClaimAttachmentsResponseDto  saveClaimAttachment(MultipartFile file, String claimUuid, JwtUser jwtUser, int createdByteam,
			int attachmentTypeId) throws Exception {
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
		ClaimAttachmentsResponseDto response = null;
		ClaimAttachmentsResponseDto.File inner=null;
		if (fullPathName != null) {
			rcmClaims = claimRepo.findByClaimUuid(claimUuid);
			if (rcmClaims == null) {
				return ClaimAttachmentsResponseDto.builder().msg(MessageConstants.CLAIM_NOT_EXIST).status(false)
						.build();
			}
			if (!rcmClaims.isPending()) {
				return ClaimAttachmentsResponseDto.builder().msg(MessageConstants.CLAIM_ALREADY_SUBMITTED).status(false).build();
			}
			try {
				in = file.getInputStream();
				attachmentType = attachmentTypeRepo.findById(attachmentTypeId);
				if (attachmentType.isPresent() && !file.isEmpty()) {
					team = new RcmTeam();
					claimAttachment = new RcmClaimAttachment();
					loginUser = userRepo.findByEmail(jwtUser.getUsername());
					fileName = Integer.toString(attachmentTypeId).concat(Constants.HYPHEN)
							.concat(file.getOriginalFilename());
					fileCounts=attachmentRepo.fileCount(fileName ,claimUuid);
					team.setId(createdByteam);
					claimAttachment.setCreatedBy(loginUser);
					claimAttachment.setCreatedByteam(team);
					claimAttachment.setFileName(fileName);
					claimAttachment.setFileLocation(fullPathName);
					claimAttachment.setStatus(true);
					claimAttachment.setUpdatedBy(loginUser);
					claimAttachment.setCreatedDate(Timestamp.from(Instant.now()));
					claimAttachment.setAtchType(attachmentType.orElse(null));
					claimAttachment.setUuid(rcmClaims);
					if (fileCounts<1) {
						inner=new ClaimAttachmentsResponseDto().new File();
						
						//save attachments data
						claimAttachment=attachmentRepo.save(claimAttachment);
						
						// Will Make folder with the help of claimUuid to save each file separatlty
						claimAttachmentFolder = utilService
								.getFileAbsolutePath(attachmentDirPath.concat(File.separator).concat(claimUuid));

						Files.copy(in, Paths.get(claimAttachmentFolder.concat(File.separator).concat(fileName)),
								StandardCopyOption.REPLACE_EXISTING);
						
	
						//save attachments count in rcm table
						
						logger.info("Previous Counts:"+rcmClaims.getAttachmentCount());
						attachmentRepo.updateAttachmentCountInRcmClaim(claimUuid, rcmClaims.getAttachmentCount()+1);
						inner.setName(file.getOriginalFilename());				
						response = ClaimAttachmentsResponseDto.builder().msg(MessageConstants.FILE_UPLOAD_SUCCESS)
								.id(claimAttachment.getId())
								.attachmentId(attachmentType.get().getId())
								.file(inner).status(true).isDeleted(claimAttachment.isDeleted()).build();
					}
					if (fileCounts >= 1) {
						response = ClaimAttachmentsResponseDto.builder().msg(MessageConstants.FILE_UPLOAD_SUCCESS).status(true).build();
					}
				} else
					response =ClaimAttachmentsResponseDto.builder().msg(MessageConstants.ATTACHMENT_TYPE_NOT_EXIST).status(false).build();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				response = ClaimAttachmentsResponseDto.builder().msg(null).status(false).build();
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
		RcmClaims claims = null;
		int deleteCount = 0;
		List<RcmClaimAttachmentDto> data = attachmentRepo.findByAttachmentsById(dto.getClaimAttachmentId(),
				dto.getClaimUuid());
		if (!data.isEmpty()) {
			for (RcmClaimAttachmentDto d : data) {
				int status = 0;
				String checkEmptyFileName = d.getFileName();
				if (checkEmptyFileName == null || checkEmptyFileName.isEmpty()) {
					response = FileResponseDto.builder().msg("Empty FileName").fileResponseStatus(false).build();
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
							dto.getClaimUuid());
					if (renameFileData == null) {
						boolean isTrue = existingFile.renameTo(renameFile);
						if (isTrue) {
							status = attachmentRepo.updateAttachmentStatusById(loginUser, d.getId(), renameFileName);
							if (status > 0) {
								++deleteCount;
								response = FileResponseDto.builder().msg(MessageConstants.RECORDS_UPDATE)
										.fileResponseStatus(true).build();
							}
						}

					} else {
						String reName = Constants.REMOVE_ATTACHMENT_PREFIX.concat(renameFileData.getRenameFile());
						File replaceFile = new File(attachmentDirPath.concat(File.separator).concat(dto.getClaimUuid())
								.concat(File.separator).concat(reName));
						boolean isTrue = existingFile.renameTo(replaceFile);
						if (isTrue) {
							status = attachmentRepo.updateAttachmentStatusById(loginUser, d.getId(), reName);
							if (status > 0) {
								++deleteCount;
								response = FileResponseDto.builder().msg(MessageConstants.RECORDS_UPDATE)
										.fileResponseStatus(true).build();
							}
						}

					}

					if (status <= 0) {
						response = FileResponseDto.builder().msg(MessageConstants.UPDATION_FAIL)
								.fileResponseStatus(false).build();
						return response;
					}
				} else {
					response = FileResponseDto.builder().msg(MessageConstants.FILE_NOT_EXIST).fileResponseStatus(false)
							.build();
					return response;
				}
			}

			// update attachements count in rcm claim table

			if (deleteCount > 0 ) {
				claims = claimRepo.findByClaimUuid(dto.getClaimUuid());
				logger.info("Previous Counts:"+claims.getAttachmentCount());
				if (claims != null) {
					attachmentRepo.updateAttachmentCountInRcmClaim(dto.getClaimUuid(),
							claims.getAttachmentCount() == 0 ? 0 : claims.getAttachmentCount()-deleteCount);
					logger.info("Total files deleted:"+deleteCount);
				}
			}
		} else
			response = FileResponseDto.builder().msg(MessageConstants.RECORD_NOT_EXIST).fileResponseStatus(false)
					.build();

		return response;
	}

	public Object[] getAttachmentFile(int id) throws Exception {
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
