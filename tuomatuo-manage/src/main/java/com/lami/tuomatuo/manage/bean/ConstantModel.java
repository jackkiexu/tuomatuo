package com.lami.tuomatuo.manage.bean;

import org.springframework.beans.factory.annotation.Value;

public class ConstantModel {
	@Value(value="${bsBindRelation}")
	public String bsBindRelation;
	@Value(value="${bsBindRelation_id}")
	public String bsBindRelation_id;
	@Value(value="${bsBindRelation_spId}")
	public String bsBindRelation_spId;
	@Value(value="${bsBindRelation_vm}")
	public String bsBindRelation_vm;
	@Value(value="${bsBindRelation_fm}")
	public String bsBindRelation_fm;
	@Value(value="${bsBindRelation_tm}")
	public String bsBindRelation_tm;
	@Value(value="${bsBindRelation_status}")
	public String bsBindRelation_status;
	@Value(value="${bsBindRelation_onOff}")
	public String bsBindRelation_onOff;
	@Value(value="${bsBindRelation_isDelete}")
	public String bsBindRelation_isDelete;
	@Value(value="${bsBindRelation_createTime}")
	public String bsBindRelation_createTime;
	@Value(value="${bsBindRelation_updateTime}")
	public String bsBindRelation_updateTime;
	@Value(value="${bsBindRelation_bindTime}")
	public String bsBindRelation_bindTime;
	@Value(value="${bsBindRelation_spSeqId}")
	public String bsBindRelation_spSeqId;
	
	
	@Value(value="${relation}")
	public String relation;
	@Value(value="${relation_id}")
	public String relation_id;
	@Value(value="${relation_spId}")
	public String relation_spId;
	@Value(value="${relation_bindMobile}")
	public String relation_bindMobile;
	@Value(value="${relation_virtualMobile}")
	public String relation_virtualMobile;
	@Value(value="${relation_status}")
	public String relation_status;
	@Value(value="${relation_createTime}")
	public String relation_createTime;
	@Value(value="${relation_updateTime}")
	public String relation_updateTime;
	@Value(value="${relation_isDelete}")
	public String relation_isDelete;
	@Value(value="${relation_onOff}")
	public String relation_onOff;
	@Value(value="${relation_correspondingID}")
	public String relation_correspondingID;
	
	@Value(value="${callRecord}")
	public String callRecord;
	@Value(value="${callRecord_id}")
	public String callRecord_id;
	@Value(value="${callRecord_userId}")
	public String callRecord_userId;
	@Value(value="${callRecord_fromNumber}")
	public String callRecord_fromNumber;
	@Value(value="${callRecord_toNumber}")
	public String callRecord_toNumber;
	@Value(value="${callRecord_vm}")
	public String callRecord_vm;
	@Value(value="${callRecord_status}")
	public String callRecord_status;
	@Value(value="${callRecord_startTime}")
	public String callRecord_startTime;
	@Value(value="${callRecord_endTime}")
	public String callRecord_endTime;
	@Value(value="${callRecord_isDelete}")
	public String callRecord_isDelete;
	@Value(value="${callRecord_callMinutes}")
	public String callRecord_callMinutes;
	@Value(value="${callRecord_errorMsg}")
	public String callRecord_errorMsg;
	@Value(value="${callRecord_spId}")
	public String callRecord_spId;
	@Value(value="${callRecord_spSeqId}")
	public String callRecord_spSeqId;
	
	@Value(value="${chargeRecord}")
	public String chargeRecord;
	@Value(value="${chargeRecord_id}")
	public String chargeRecord_id;
	@Value(value="${chargeRecord_userId}")
	public String chargeRecord_userId;
	@Value(value="${chargeRecord_payAccount}")
	public String chargeRecord_payAccount;
	@Value(value="${chargeRecord_mobile}")
	public String chargeRecord_mobile;
	@Value(value="${chargeRecord_status}")
	public String chargeRecord_status;
	@Value(value="${chargeRecord_type}")
	public String chargeRecord_type;
	@Value(value="${chargeRecord_createTime}")
	public String chargeRecord_createTime;
	@Value(value="${chargeRecord_price}")
	public String chargeRecord_price;
	@Value(value="${chargeRecord_openId}")
	public String chargeRecord_openId;
	@Value(value="${chargeRecord_updateTime}")
	public String chargeRecord_updateTime;
	@Value(value="${chargeRecord_errorMsg}")
	public String chargeRecord_errorMsg;
	@Value(value="${chargeRecord_serviceIds}")
	public String chargeRecord_serviceIds;
	@Value(value="${chargeRecord_isDelete}")
	public String chargeRecord_isDelete;
	
	
	@Value(value="${messageRecord}")
	public String messageRecord;
	@Value(value="${messageRecord_id}")
	public String messageRecord_id;
	@Value(value="${messageRecord_userId}")
	public String messageRecord_userId;
	@Value(value="${messageRecord_fromNumber}")
	public String messageRecord_fromNumber;
	@Value(value="${messageRecord_toNumber}")
	public String messageRecord_toNumber;
	@Value(value="${messageRecord_content}")
	public String messageRecord_content;
	@Value(value="${messageRecord_createTime}")
	public String messageRecord_createTime;
	@Value(value="${messageRecord_updateTime}")
	public String messageRecord_updateTime;
	@Value(value="${messageRecord_sendStatus}")
	public String messageRecord_sendStatus;
	@Value(value="${messageRecord_receiveStatus}")
	public String messageRecord_receiveStatus;
	@Value(value="${messageRecord_isDelete}")
	public String messageRecord_isDelete;
	@Value(value="${messageRecord_errorMsg}")
	public String messageRecord_errorMsg;
	@Value(value="${messageRecord_messageType}")
	public String messageRecord_messageType;
	
	@Value(value="${numbers}")
	public String numbers;
	@Value(value="${numbers_id}")
	public String numbers_id;
	@Value(value="${numbers_mobile}")
	public String numbers_mobile;
	@Value(value="${numbers_status}")
	public String numbers_status;
	@Value(value="${numbers_operator}")
	public String numbers_operator;
	@Value(value="${numbers_area}")
	public String numbers_area;
	@Value(value="${numbers_weight}")
	public String numbers_weight;
	@Value(value="${numbers_effect}")
	public String numbers_effect;
	@Value(value="${numbers_type}")
	public String numbers_type;
	@Value(value="${numbers_createTime}")
	public String numbers_createTime;
	@Value(value="${numbers_flag}")
	public String numbers_flag;
	@Value(value="${numbers_callChannel}")
	public String numbers_callChannel;
	@Value(value="${numbers_smsChannel}")
	public String numbers_smsChannel;
	@Value(value="${numbers_sort}")
	public String numbers_sort;
	@Value(value="${numbers_spId}")
	private String numbers_spId;
	@Value(value="${numbers_inCache}")
	private String numbers_inCache;
	
	
	@Value(value="${services}")
	public String services;
	@Value(value="${services_id}")
	public String services_id;
	@Value(value="${services_name}")
	public String services_name;
	@Value(value="${services_price}")
	public String services_price;
	@Value(value="${services_type}")
	public String services_type;
	@Value(value="${services_status}")
	public String services_status;
	@Value(value="${services_createTime}")
	public String services_createTime;
	@Value(value="${services_startTime}")
	public String services_startTime;
	@Value(value="${services_endTime}")
	public String services_endTime;
	@Value(value="${services_maxMsgCount}")
	public String services_maxMsgCount;
	@Value(value="${services_maxCallMinutes}")
	public String services_maxCallMinutes;
	@Value(value="${services_personIssuedCount}")
	public String services_personIssuedCount;
	@Value(value="${services_personIssuedFlag}")
	public String services_personIssuedFlag;
	@Value(value="${services_platformFlag}")
	public String services_platformFlag;
	@Value(value="${services_delayDays}")
	public String services_delayDays;
	@Value(value="${services_delayMinutes}")
	public String services_delayMinutes;
	@Value(value="${services_mobilePool}")
	public String services_mobilePool;
	@Value(value="${user}")
	public String user;
	@Value(value="${user_id}")
	public String user_id;
	@Value(value="${user_loginName}")
	public String user_loginName;
	@Value(value="${user_password}")
	public String user_password;
	@Value(value="${user_mail}")
	public String user_mail;
	@Value(value="${user_openId}")
	public String user_openId;
	@Value(value="${user_status}")
	public String user_status;
	@Value(value="${user_createTime}")
	public String user_createTime;
	@Value(value="${user_updateTime}")
	public String user_updateTime;
	@Value(value="${user_lastLoginTime}")
	public String user_lastLoginTime;
	@Value(value="${user_sendSecretTime}")
	public String user_sendSecretTime;
	@Value(value="${user_mailSecret}")
	public String user_mailSecret;
	@Value(value="${user_fromType}")
	public String user_fromType;
	@Value(value="${user_sendDelayMsgFlag}")
	public String user_sendDelayMsgFlag;
	@Value(value="${user_countNumber}")
	public String user_countNumber;
	@Value(value="${user_endTime}")
	public String user_endTime;
	@Value(value="${userLog}")
	public String userLog;
	@Value(value="${userLog_id}")
	public String userLog_id;
	@Value(value="${userLog_userId}")
	public String userLog_userId;
	@Value(value="${userLog_userName}")
	public String userLog_userName;
	@Value(value="${userLog_createTime}")
	public String userLog_createTime;
	@Value(value="${userLog_active}")
	public String userLog_active;
	@Value(value="${userLog_isDelete}")
	public String userLog_isDelete;
	@Value(value="${userLog_deleteDes}")
	public String userLog_deleteDes;
	@Value(value="${userLog_activeDes}")
	public String userLog_activeDes;
	@Value(value="${userNumber}")
	public String userNumber;
	@Value(value="${userNumber_id}")
	public String userNumber_id;
	@Value(value="${userNumber_userId}")
	public String userNumber_userId;
	@Value(value="${userNumber_mobile}")
	public String userNumber_mobile;
	@Value(value="${userNumber_redirectMobile}")
	public String userNumber_redirectMobile;
	@Value(value="${userNumber_status}")
	public String userNumber_status;
	@Value(value="${userNumber_createTime}")
	public String userNumber_createTime;
	@Value(value="${userNumber_endTime}")
	public String userNumber_endTime;
	@Value(value="${userNumber_bootTime}")
	public String userNumber_bootTime;
	@Value(value="${userNumber_shutTime}")
	public String userNumber_shutTime;
	@Value(value="${userNumber_isDelete}")
	public String userNumber_isDelete;
	@Value(value="${userNumber_msgStartTime}")
	public String userNumber_msgStartTime;
	@Value(value="${userNumber_msgEndTime}")
	public String userNumber_msgEndTime;
	@Value(value="${userNumber_currStatus}")
	public String userNumber_currStatus;
	@Value(value="userNumber_isRedirect")
	public String userNumber_isRedirect;
	
	@Value(value="${userServices}")
	public String userServices;
	@Value(value="${userServices_id}")
	public String userServices_id;
	@Value(value="${userServices_userId}")
	public String userServices_userId;
	@Value(value="${userServices_serviceId}")
	public String userServices_serviceId;
	@Value(value="${userServices_createTime}")
	public String userServices_createTime;
	@Value(value="${userServices_currentMsgCount}")
	public String userServices_currentMsgCount;
	@Value(value="${userServices_currentCallMinutes}")
	public String userServices_currentCallMinutes;
	@Value(value="${userServices_weight}")
	public String userServices_weight;
	@Value(value="${userServices_isDelete}")
	public String userServices_isDelete;
	@Value(value = "${userServices_status}")
	public String userServices_status;
	@Value(value="${sysOperator}")
	public String sysOperator;
	@Value(value="${sysOperator_id}")
	public String sysOperator_id;
	@Value(value="${sysOperator_login}")
	public String sysOperator_login;
	@Value(value="${sysOperator_password}")
	public String sysOperator_password;
	@Value(value="${sysOperator_name}")
	public String sysOperator_name;
	@Value(value="${sysOperator_telephone}")
	public String sysOperator_telephone;
	@Value(value="${sysOperator_status}")
	public String sysOperator_status;
	@Value(value="${sysOperator_createTime}")
	public String sysOperator_createTime;
	@Value(value="${sysOperator_updateTime}")
	public String sysOperator_updateTime;
	@Value(value="${sysOperator_lastLoginTime}")
	public String sysOperator_lastLoginTime;
	@Value(value="${sysOperator_checkIp}")
	public String sysOperator_checkIp;
	@Value(value="${sysOperator_ip}")
	public String sysOperator_ip;
	
	@Value(value="${sysOperatorRole}")
	public String sysOperatorRole;
	@Value(value="${sysOperatorRole_id}")
	public String sysOperatorRole_id;
	@Value(value="${sysOperatorRole_roleId}")
	public String sysOperatorRole_roleId;
	@Value(value="${sysOperatorRole_operatorId}")
	public String sysOperatorRole_operatorId;
	@Value(value="${sysRole}")
	public String sysRole;
	@Value(value="${sysRole_id}")
	public String sysRole_id;
	@Value(value="${sysRole_name}")
	public String sysRole_name;
	@Value(value="${sysRole_details}")
	public String sysRole_details;
	@Value(value="${sysRolePermission}")
	public String sysRolePermission;
	@Value(value="${sysRolePermission_id}")
	public String sysRolePermission_id;
	@Value(value="${sysRolePermission_roleId}")
	public String sysRolePermission_roleId;
	@Value(value="${sysRolePermission_permissionId}")
	public String sysRolePermission_permissionId;
	@Value(value="${sysPermission}")
	public String sysPermission;
	@Value(value="${sysPermission_id}")
	public String sysPermission_id;
	@Value(value="${sysPermission_name}")
	public String sysPermission_name;
	@Value(value="${sysPermission_path}")
	public String sysPermission_path;
	@Value(value="${sysPermission_methods}")
	public String sysPermission_methods;
	@Value(value="${sysPermission_menu}")
	public String sysPermission_menu;
	@Value(value="${sysPermission_parentId}")
	public String sysPermission_parentId;
	
	@Value(value="${sysOperatorLog}")
	public String sysOperatorLog;
	@Value(value="${sysOperatorLog_id}")
	public String sysOperatorLog_id;
	@Value(value="${sysOperatorLog_operatorId}")
	public String sysOperatorLog_operatorId;
	@Value(value="${sysOperatorLog_createTime}")
	public String sysOperatorLog_createTime;
	@Value(value="${sysOperatorLog_active}")
	public String sysOperatorLog_active;
	@Value(value="${sysOperatorLog_isDelete}")
	public String sysOperatorLog_isDelete;
	
	@Value(value="${comments}")
	public String comments;
	@Value(value="${comments_id}")
	public String comments_id;
	@Value(value="${comments_userId}")
	public String comments_userId;
	@Value(value="${comments_content}")
	public String comments_content;
	@Value(value="${comments_mobile}")
	public String comments_mobile;
	@Value(value="${comments_createTime}")
	public String comments_createTime;
	@Value(value="${comments_updateTime}")
	public String comments_updateTime;
	@Value(value="${comments_missCount}")
	public String comments_missCount;
	@Value(value="${comments_receivedId}")
	public String comments_receivedId;
	
	
	@Value(value="${stationLetter_id}")
	private String stationLetter_id;
	@Value(value="${stationLetter_createTime}")
	private String stationLetter_createTime;
	@Value(value="${stationLetter_content}")
	private String stationLetter_content;
	@Value(value="${stationLetter_type}")
	private String stationLetter_type;
	@Value(value="${stationLetter_status}")
	private String stationLetter_status;
	@Value(value="${stationLetter_fromUserId}")
	private String stationLetter_fromUserId;
	@Value(value="${stationLetter_fromNumber}")
	private String stationLetter_fromNumber;
	@Value(value="${stationLetter_toUserId}")
	private String stationLetter_toUserId;
	@Value(value="${stationLetter_toNumber}")
	private String stationLetter_toNumber;
	@Value(value="${stationLetter_latestId}")
	private String stationLetter_latestId;
	
	@Value(value="${codes}")
	private String codes;
	@Value(value="${codes_id}")
	private String codes_id;
	@Value(value="${codes_codes}")
	private String codes_codes;
	@Value(value="${codes_status}")
	private String codes_status;
	@Value(value="${codes_serviceId}")
	private String codes_serviceId;
	@Value(value="${codes_userId}")
	private String codes_userId;
	@Value(value="${codes_useTime}")
	private String codes_useTime;
	@Value(value="${codes_createTime}")
	private String codes_createTime;
	@Value(value="${codes_sellerId}")
	private String codes_sellerId;
	@Value(value="${codes_endTime}")
	private String codes_endTime;
	@Value(value="${codes_isDelete}")
	private String codes_isDelete;
	@Value(value="${codes_codesDes}")
	private String codes_codesDes;
	
	@Value(value="${tele}")
	private String tele;
	@Value(value="${tele_id}")
	private String tele_id;
	@Value(value="${tele_name}")
	private String tele_name;
	@Value(value="${tele_key}")
	private String tele_key;
	@Value(value="${tele_createTime}")
	private String tele_createTime;
	@Value(value="${tele_updateTime}")
	private String tele_updateTime;
	@Value(value="${tele_status}")
	private String tele_status;
	@Value(value="${tele_notifyCallUrl}")
	private String tele_notifyCallUrl;
	@Value(value="${tele_notifyMtReceUrl}")
	private String tele_notifyMtReceUrl;
	@Value(value="${tele_notifyMoUrl}")
	private String tele_notifyMoUrl;
	@Value(value="${tele_isDelete}")
	private String tele_isDelete;
	@Value(value="${tele_generalFlow}")
	private String tele_generalFlow;
	@Value(value="${tele_msgFlow}")
	private String tele_msgFlow;
	@Value(value="${tele_callFlow}")
	private String tele_callFlow;
	@Value(value="${tele_validIp}")
	private String tele_validIp;
	@Value(value="${tele_needVerifyIp}")
	private String tele_needVerifyIp;
	@Value(value="${tele_checkAuthFm}")
	private String tele_checkAuthFm;
	@Value(value="${tele_checkAuthFmTimeout}")
	private String tele_checkAuthFmTimeout;
	@Value(value="${tele_display}")
	private String tele_display;
	@Value(value="${tele_authFmUrl}")
	private String tele_authFmUrl;
	@Value(value="${tele_callDirectionUrl}")
	private String tele_callDirectionUrl;
	@Value(value="${tele_notifyMediaStreamUrl}")
	private String tele_notifyMediaStreamUrl;
	@Value(value="${tele_dmpCallUrl}")
	private String tele_dmpCallUrl;
	@Value(value="${tele_dmpMtReceUrl}")
	private String tele_dmpMtReceUrl;
	@Value(value="${tele_dmpMoUrl}")
	private String tele_dmpMoUrl;
	@Value(value="${tele_notifyAudioStatusUrl}")
	private String tele_notifyAudioStatusUrl;
	@Value(value="${tele_dmpRecordUrl}")
	private String tele_dmpRecordUrl;
	
	@Value(value="${systemConfig}")
	public String systemConfig;
	@Value(value="${systemConfig_id}")
	public String systemConfig_id;
	@Value(value="${systemConfig_name}")
	public String systemConfig_name;
	@Value(value="${systemConfig_details}")
	public String systemConfig_details;
	@Value(value="${systemConfig_value}")
	public String systemConfig_value;
	
	
	@Value(value="${blockMobile_id}")
	public String blockMobile_id;
	@Value(value="${blockMobile_fromNumber}")
	public String blockMobile_fromNumber;
	@Value(value="${blockMobile_vmNumber}")
	public String blockMobile_vmNumber;
	@Value(value="${blockMobile_status}")
	public String blockMobile_status;
	@Value(value="${blockMobile_createTime}")
	public String blockMobile_createTime;
	@Value(value="${blockMobile_updateTime}")
	public String blockMobile_updateTime;
	@Value(value="${blockMobile_isDelete}")
	public String blockMobile_isDelete;
	@Value(value="${blockMobile_userId}")
	public String blockMobile_userId;
	@Value(value="${blockMobile_callStrategy}")
	public String blockMobile_callStrategy;
	@Value(value="${blockMobile_msgStrategy}")
	public String blockMobile_msgStrategy;
	
	@Value(value="${globalBlockMobile_id}")
	public String globalBlockMobile_id;
	@Value(value="${globalBlockMobile_fm}")
	public String globalBlockMobile_fm;
	@Value(value="${globalBlockMobile_status}")
	public String globalBlockMobile_status;
	@Value(value="${globalBlockMobile_createTime}")
	public String globalBlockMobile_createTime;
	@Value(value="${globalBlockMobile_updateTime}")
	public String globalBlockMobile_updateTime;
	@Value(value="${globalBlockMobile_isDelete}")
	public String globalBlockMobile_isDelete;
	
	@Value(value="${voiceServices_id}")
	public String voiceServices_id;
	@Value(value="${voiceServices_soundName}")
	public String voiceServices_soundName;
	@Value(value="${voiceServices_memoryAddress}")
	public String voiceServices_memoryAddress;
	@Value(value="${voiceServices_playAddress}")
	public String voiceServices_playAddress;
	@Value(value="${voiceServices_uploadUser}")
	public String voiceServices_uploadUser;
	@Value(value="${voiceServices_platForm}")
	public String voiceServices_platForm;
	@Value(value="${voiceServices_isDelete}")
	public String voiceServices_isDelete;
	@Value(value="${voiceServices_type}")
	public String voiceServices_type;
	@Value(value="${voiceServices_createTime}")
	public String voiceServices_createTime;
	@Value(value="${voiceServices_status}")
	public String voiceServices_status;
	@Value(value="${voiceServices_formats}")
	public String voiceServices_formats;
	
	@Value(value="${keyWord_id}")
	public String keyWord_id;
	@Value(value="${keyWord_keyName}")
	public String keyWord_keyName;
	@Value(value="${keyWord_createTime}")
	public String keyWord_createTime;
	@Value(value="${keyWord_type}")
	public String keyWord_type;
	@Value(value="${keyWord_createUser}")
	public String keyWord_createUser;
	
	
	@Value(value="${callRepeatVO_mobileNumber}")
	public String callRepeatVO_mobileNumber;
	@Value(value="${callRepeatVO_repeatCount}")
	public String callRepeatVO_repeatCount;
	@Value(value="${callRepeatVO_userId}")
	public String callRepeatVO_userId;
	
	@Value(value="${messageRepeatVO_mobileNumber}")
	public String messageRepeatVO_mobileNumber;
	@Value(value="${messageRepeatVO_repeatCount}")
	public String messageRepeatVO_repeatCount;
	@Value(value="${messageRepeatVO_userId}")
	public String messageRepeatVO_userId;
	
	
	@Value(value="${userServicesRepeatVO_loginName}")
	public String userServicesRepeatVO_loginName;
	@Value(value="${userServicesRepeatVO_repeatCount}")
	public String userServicesRepeatVO_repeatCount;
	@Value(value="${userServicesRepeatVO_userId}")
	public String userServicesRepeatVO_userId;
	
	
	
	@Value(value="${messageContainWords}")
	public String messageContainWords;
	@Value(value="${messageContainWords_id}")
	public String messageContainWords_id;
	@Value(value="${messageContainWords_userId}")
	public String messageContainWords_userId;
	@Value(value="${messageContainWords_fromNumber}")
	public String messageContainWords_fromNumber;
	@Value(value="${messageContainWords_toNumber}")
	public String messageContainWords_toNumber;
	@Value(value="${messageContainWords_content}")
	public String messageContainWords_content;
	@Value(value="${messageContainWords_createTime}")
	public String messageContainWords_createTime;
	@Value(value="${messageContainWords_sendStatus}")
	public String messageContainWords_sendStatus;
	@Value(value="${messageContainWords_receiveStatus}")
	public String messageContainWords_receiveStatus;
	@Value(value="${messageContainWords_isDelete}")
	public String messageContainWords_isDelete;
	@Value(value="${messageContainWords_errorMsg}")
	public String messageContainWords_errorMsg;
	@Value(value="${messageContainWords_messageType}")
	public String messageContainWords_messageType;
	
	@Value(value="${voiceToken_id}")
	public String voiceToken_id;
	@Value(value="${voiceToken_token}")
	public String voiceToken_token;
	@Value(value="${voiceToken_endtime}")
	public String voiceToken_endtime;
	
	
	@Value(value="${speechSoundBusiness_id}")
	public String speechSoundBusiness_id;
	@Value(value="${speechSoundBusiness_fromNumber}")
	public String speechSoundBusiness_fromNumber;
	@Value(value="${speechSoundBusiness_toNumber}")
	public String speechSoundBusiness_toNumber;
	@Value(value="${speechSoundBusiness_endTime}")
	public String speechSoundBusiness_endTime;
	@Value(value="${speechSoundBusiness_startTime}")
	public String speechSoundBusiness_startTime;
	@Value(value="${speechSoundBusiness_virtualNumber}")
	public String speechSoundBusiness_virtualNumber;
	@Value(value="${speechSoundBusiness_voiceType}")
	public String speechSoundBusiness_voiceType;
	@Value(value="${speechSoundBusiness_isDelete}")
	public String speechSoundBusiness_isDelete;
	@Value(value="${speechSoundBusiness_storagePath}")
	public String speechSoundBusiness_storagePath;
	
	

	
	@Value(value="${serverOnlineData_id}")
	public String serverOnlineData_id;
	@Value(value="${serverOnlineData_serverIO}")
	public String serverOnlineData_serverIO;
	@Value(value="${serverOnlineData_serverIP}")
	public String serverOnlineData_serverIP;
	@Value(value="${serverOnlineData_serverMen}")
	public String serverOnlineData_serverMen;
	@Value(value="${serverOnlineData_serverDisk}")
	public String serverOnlineData_serverDisk;
	@Value(value="${serverOnlineData_serverName}")
	public String serverOnlineData_serverName;
	@Value(value="${serverOnlineData_cpuOne}")
	public String serverOnlineData_cpuOne;
	@Value(value="${serverOnlineData_cpuTwo}")
	public String serverOnlineData_cpuTwo;
	@Value(value="${serverOnlineData_cpuThree}")
	public String serverOnlineData_cpuThree;
	@Value(value="${serverOnlineData_createTime}")
	public String serverOnlineData_createTime;
	
	@Value(value="${serverCardData_id}")
	public String serverCardData_id;
	@Value(value="${serverCardData_serverTX}")
	public String serverCardData_serverTX;
	@Value(value="${serverCardData_serverRX}")
	public String serverCardData_serverRX;
	@Value(value="${serverCardData_serverTime}")
	public String serverCardData_serverTime;
	@Value(value="${serverCardData_serverIP}")
	public String serverCardData_serverIP;
	@Value(value="${serverCardData_interface_name}")
	public String serverCardData_interface_name;
	
	
	@Value(value="${whiteKeyWord_id}")
	public String whiteKeyWord_id;
	@Value(value="${whiteKeyWord_keyWord}")
	public String whiteKeyWord_keyWord;
	@Value(value="${whiteKeyWord_createTime}")
	public String whiteKeyWord_createTime;
	@Value(value="${whiteKeyWord_createUser}")
	public String whiteKeyWord_createUser;
	@Value(value="${whiteKeyWord_isDelete}")
	public String whiteKeyWord_isDelete;
	
	
	@Value(value="${whiteNumberKey_id}")
	public String whiteNumberKey_id;
	@Value(value="${whiteNumberKey_number}")
	public String whiteNumberKey_number;
	@Value(value="${whiteNumberKey_createTime}")
	public String whiteNumberKey_createTime;
	@Value(value="${whiteNumberKey_createUser}")
	public String whiteNumberKey_createUser;
	@Value(value="${whiteNumberKey_isDelete}")
	public String whiteNumberKey_isDelete;
	
	@Value(value="${blockNumberKey_id}")
	public String blockNumberKey_id;
	@Value(value="${blockNumberKey_number}")
	public String blockNumberKey_number;
	@Value(value="${blockNumberKey_createTime}")
	public String blockNumberKey_createTime;
	@Value(value="${blockNumberKey_createUser}")
	public String blockNumberKey_createUser;
	@Value(value="${blockNumberKey_isDelete}")
	public String blockNumberKey_isDelete;
	
	
	
	@Value(value="${blockKeyWord_id}")
	public String blockKeyWord_id;
	@Value(value="${blockKeyWord_keyWord}")
	public String blockKeyWord_keyWord;
	@Value(value="${blockKeyWord_createTime}")
	public String blockKeyWord_createTime;
	@Value(value="${blockKeyWord_createUser}")
	public String blockKeyWord_createUser;
	@Value(value="${blockKeyWord_isDelete}")
	public String blockKeyWord_isDelete;

	
			
	@Value(value="${mediaFile_id}")
	public String mediaFile_id;
	@Value(value="${mediaFile_fm}")
	public String mediaFile_fm;
	@Value(value="${mediaFile_tm}")
	public String mediaFile_tm;
	@Value(value="${mediaFile_vm}")
	public String mediaFile_vm;
	@Value(value="${mediaFile_readUrl}")
	public String mediaFile_readUrl;
	@Value(value="${mediaFile_isDelete}")
	public String mediaFile_isDelete;
	@Value(value="${mediaFile_callRecordId}")
	public String mediaFile_callRecordId;
	@Value(value="${mediaFile_spId}")
	public String mediaFile_spId;
	@Value(value="${mediaFile_startTime}")
	public String mediaFile_startTime;
	@Value(value="${mediaFile_endTime}")
	public String mediaFile_endTime;
	@Value(value="${mediaFile_tag}")
	public String mediaFile_tag;
	
	
	
	
	public String getTele_display() {
		return tele_display;
	}
	public String getMediaFile_id() {
		return mediaFile_id;
	}
	public String getMediaFile_fm() {
		return mediaFile_fm;
	}
	public String getMediaFile_tm() {
		return mediaFile_tm;
	}
	public String getMediaFile_vm() {
		return mediaFile_vm;
	}
	public String getMediaFile_readUrl() {
		return mediaFile_readUrl;
	}
	public String getMediaFile_isDelete() {
		return mediaFile_isDelete;
	}
	public String getMediaFile_callRecordId() {
		return mediaFile_callRecordId;
	}
	public String getMediaFile_spId() {
		return mediaFile_spId;
	}
	public String getMediaFile_startTime() {
		return mediaFile_startTime;
	}
	public String getMediaFile_endTime() {
		return mediaFile_endTime;
	}
	public String getMediaFile_tag() {
		return mediaFile_tag;
	}
	
	
	
	
	public String getBlockNumberKey_id() {
		return blockNumberKey_id;
	}
	public String getBlockNumberKey_number() {
		return blockNumberKey_number;
	}
	public String getBlockNumberKey_createTime() {
		return blockNumberKey_createTime;
	}
	public String getBlockNumberKey_createUser() {
		return blockNumberKey_createUser;
	}
	public String getBlockNumberKey_isDelete() {
		return blockNumberKey_isDelete;
	}
	public String getBlockKeyWord_id() {
		return blockKeyWord_id;
	}
	public String getBlockKeyWord_keyWord() {
		return blockKeyWord_keyWord;
	}
	public String getBlockKeyWord_createTime() {
		return blockKeyWord_createTime;
	}
	public String getBlockKeyWord_createUser() {
		return blockKeyWord_createUser;
	}
	public String getBlockKeyWord_isDelete() {
		return blockKeyWord_isDelete;
	}
	public String getWhiteNumberKey_id() {
		return whiteNumberKey_id;
	}
	public String getWhiteNumberKey_number() {
		return whiteNumberKey_number;
	}
	public String getWhiteNumberKey_createTime() {
		return whiteNumberKey_createTime;
	}
	public String getWhiteNumberKey_createUser() {
		return whiteNumberKey_createUser;
	}
	public String getWhiteNumberKey_isDelete() {
		return whiteNumberKey_isDelete;
	}
	public String getWhiteKeyWord_id() {
		return whiteKeyWord_id;
	}
	public String getWhiteKeyWord_keyWord() {
		return whiteKeyWord_keyWord;
	}
	public String getWhiteKeyWord_createTime() {
		return whiteKeyWord_createTime;
	}
	public String getWhiteKeyWord_createUser() {
		return whiteKeyWord_createUser;
	}
	public String getWhiteKeyWord_isDelete() {
		return whiteKeyWord_isDelete;
	}
	public String getTele_checkAuthFm() {
		return tele_checkAuthFm;
	}
	public String getServerCardData_interface_name() {
		return serverCardData_interface_name;
	}
	public String getServerOnlineData_serverIP() {
		return serverOnlineData_serverIP;
	}
	public String getServerCardData_id() {
		return serverCardData_id;
	}
	public String getServerCardData_serverTX() {
		return serverCardData_serverTX;
	}
	public String getServerCardData_serverRX() {
		return serverCardData_serverRX;
	}
	public String getServerCardData_serverTime() {
		return serverCardData_serverTime;
	}
	public String getServerCardData_serverIP() {
		return serverCardData_serverIP;
	}
	public String getTele_checkAuthFmTimeout() {
		return tele_checkAuthFmTimeout;
	}
	public String getServerOnlineData_id() {
		return serverOnlineData_id;
	}
	public String getServerOnlineData_serverIO() {
		return serverOnlineData_serverIO;
	}
	public String getServerOnlineData_serverMen() {
		return serverOnlineData_serverMen;
	}
	public String getServerOnlineData_serverDisk() {
		return serverOnlineData_serverDisk;
	}
	public String getServerOnlineData_serverName() {
		return serverOnlineData_serverName;
	}
	public String getServerOnlineData_cpuOne() {
		return serverOnlineData_cpuOne;
	}
	public String getServerOnlineData_cpuTwo() {
		return serverOnlineData_cpuTwo;
	}
	public String getServerOnlineData_cpuThree() {
		return serverOnlineData_cpuThree;
	}
	public String getServerOnlineData_createTime() {
		return serverOnlineData_createTime;
	}
	public String getSpeechSoundBusiness_id() {
		return speechSoundBusiness_id;
	}
	public String getSpeechSoundBusiness_fromNumber() {
		return speechSoundBusiness_fromNumber;
	}
	public String getSpeechSoundBusiness_toNumber() {
		return speechSoundBusiness_toNumber;
	}
	public String getSpeechSoundBusiness_endTime() {
		return speechSoundBusiness_endTime;
	}
	public String getSpeechSoundBusiness_startTime() {
		return speechSoundBusiness_startTime;
	}
	public String getSpeechSoundBusiness_virtualNumber() {
		return speechSoundBusiness_virtualNumber;
	}
	public String getSpeechSoundBusiness_voiceType() {
		return speechSoundBusiness_voiceType;
	}
	public String getSpeechSoundBusiness_isDelete() {
		return speechSoundBusiness_isDelete;
	}
	public String getSpeechSoundBusiness_storagePath() {
		return speechSoundBusiness_storagePath;
	}
	public String getUserServicesRepeatVO_loginName() {
		return userServicesRepeatVO_loginName;
	}
	public String getUserServicesRepeatVO_userId() {
		return userServicesRepeatVO_userId;
	}
	public String getUserServicesRepeatVO_repeatCount() {
		return userServicesRepeatVO_repeatCount;
	}
	
	public String getVoiceToken_id() {
		return voiceToken_id;
	}
	public String getVoiceToken_token() {
		return voiceToken_token;
	}
	public String getVoiceToken_endtime() {
		return voiceToken_endtime;
	}
	public String getMessageRepeatVO_mobileNumber() {
		return messageRepeatVO_mobileNumber;
	}
	public String getMessageRepeatVO_repeatCount() {
		return messageRepeatVO_repeatCount;
	}
	public String getMessageRepeatVO_userId() {
		return messageRepeatVO_userId;
	}
	public String getCallRepeatVO_userId() {
		return callRepeatVO_userId;
	}
	public String getCallRepeatVO_mobileNumber() {
		return callRepeatVO_mobileNumber;
	}
	public String getCallRepeatVO_repeatCount() {
		return callRepeatVO_repeatCount;
	}
	public String getMessageContainWords() {
		return messageContainWords;
	}
	public String getMessageContainWords_id() {
		return messageContainWords_id;
	}
	public String getMessageContainWords_userId() {
		return messageContainWords_userId;
	}
	public String getMessageContainWords_fromNumber() {
		return messageContainWords_fromNumber;
	}
	public String getMessageContainWords_toNumber() {
		return messageContainWords_toNumber;
	}
	public String getMessageContainWords_content() {
		return messageContainWords_content;
	}
	public String getMessageContainWords_createTime() {
		return messageContainWords_createTime;
	}
	public String getMessageContainWords_sendStatus() {
		return messageContainWords_sendStatus;
	}
	public String getMessageContainWords_receiveStatus() {
		return messageContainWords_receiveStatus;
	}
	public String getMessageContainWords_isDelete() {
		return messageContainWords_isDelete;
	}
	public String getMessageContainWords_errorMsg() {
		return messageContainWords_errorMsg;
	}
	public String getMessageContainWords_messageType() {
		return messageContainWords_messageType;
	}
	
	
	public String getVoiceServices_id() {
		return voiceServices_id;
	}
	public String getVoiceServices_soundName() {
		return voiceServices_soundName;
	}
	public String getVoiceServices_memoryAddress() {
		return voiceServices_memoryAddress;
	}
	public String getVoiceServices_playAddress() {
		return voiceServices_playAddress;
	}
	public String getVoiceServices_uploadUser() {
		return voiceServices_uploadUser;
	}
	public String getVoiceServices_platForm() {
		return voiceServices_platForm;
	}
	public String getVoiceServices_isDelete() {
		return voiceServices_isDelete;
	}
	public String getVoiceServices_type() {
		return voiceServices_type;
	}
	public String getVoiceServices_createTime() {
		return voiceServices_createTime;
	}
	public String getVoiceServices_status() {
		return voiceServices_status;
	}
	public String getVoiceServices_formats() {
		return voiceServices_formats;
	}
	
	
	
	
	
	public String getKeyWord_id() {
		return keyWord_id;
	}
	public String getKeyWord_keyName() {
		return keyWord_keyName;
	}
	public String getKeyWord_createTime() {
		return keyWord_createTime;
	}
	public String getKeyWord_type() {
		return keyWord_type;
	}
	public String getKeyWord_createUser() {
		return keyWord_createUser;
	}
	
	
	
	public String getGlobalBlockMobile_id() {
		return globalBlockMobile_id;
	}
	public String getGlobalBlockMobile_fm() {
		return globalBlockMobile_fm;
	}
	public String getGlobalBlockMobile_status() {
		return globalBlockMobile_status;
	}
	public String getGlobalBlockMobile_createTime() {
		return globalBlockMobile_createTime;
	}
	public String getGlobalBlockMobile_updateTime() {
		return globalBlockMobile_updateTime;
	}
	public String getGlobalBlockMobile_isDelete() {
		return globalBlockMobile_isDelete;
	}
	public String getBlockMobile_id() {
		return blockMobile_id;
	}
	public String getBlockMobile_fromNumber() {
		return blockMobile_fromNumber;
	}
	public String getBlockMobile_vmNumber() {
		return blockMobile_vmNumber;
	}
	public String getBlockMobile_status() {
		return blockMobile_status;
	}
	public String getBlockMobile_createTime() {
		return blockMobile_createTime;
	}
	public String getBlockMobile_updateTime() {
		return blockMobile_updateTime;
	}
	public String getBlockMobile_isDelete() {
		return blockMobile_isDelete;
	}
	public String getBlockMobile_userId() {
		return blockMobile_userId;
	}
	public String getBlockMobile_callStrategy() {
		return blockMobile_callStrategy;
	}
	public String getBlockMobile_msgStrategy() {
		return blockMobile_msgStrategy;
	}
	public String getBsBindRelation_spSeqId() {
		return bsBindRelation_spSeqId;
	}
	public String getCallRecord_spSeqId() {
		return callRecord_spSeqId;
	}
//>>>>>>> .r4568
	public String getRelation_correspondingID() {
		return relation_correspondingID;
	}
	public String getSysOperator_checkIp() {
		return sysOperator_checkIp;
	}
	public String getSysOperator_ip() {
		return sysOperator_ip;
	}
	public String getCallRecord_spId() {
		return callRecord_spId;
	}
	public String getBsBindRelation() {
		return bsBindRelation;
	}
	public String getBsBindRelation_id() {
		return bsBindRelation_id;
	}
	public String getBsBindRelation_spId() {
		return bsBindRelation_spId;
	}
	public String getBsBindRelation_vm() {
		return bsBindRelation_vm;
	}
	public String getBsBindRelation_fm() {
		return bsBindRelation_fm;
	}
	public String getBsBindRelation_tm() {
		return bsBindRelation_tm;
	}
	public String getBsBindRelation_status() {
		return bsBindRelation_status;
	}
	public String getBsBindRelation_onOff() {
		return bsBindRelation_onOff;
	}
	public String getBsBindRelation_isDelete() {
		return bsBindRelation_isDelete;
	}
	public String getBsBindRelation_createTime() {
		return bsBindRelation_createTime;
	}
	public String getBsBindRelation_updateTime() {
		return bsBindRelation_updateTime;
	}
	public String getBsBindRelation_bindTime() {
		return bsBindRelation_bindTime;
	}
	public String getTele_generalFlow() {
		return tele_generalFlow;
	}
	public String getTele_msgFlow() {
		return tele_msgFlow;
	}
	public String getTele_callFlow() {
		return tele_callFlow;
	}
	public String getTele_validIp() {
		return tele_validIp;
	}
	public String getTele_needVerifyIp() {
		return tele_needVerifyIp;
	}
	public String getTele_authFmUrl() {
		return tele_authFmUrl;
	}
	public String getTele_callDirectionUrl() {
		return tele_callDirectionUrl;
	}
	public String getTele_notifyMediaStreamUrl() {
		return tele_notifyMediaStreamUrl;
	}
	public String getTele_dmpCallUrl() {
		return tele_dmpCallUrl;
	}
	public String getTele_dmpMtReceUrl() {
		return tele_dmpMtReceUrl;
	}
	public String getTele_dmpMoUrl() {
		return tele_dmpMoUrl;
	}
	public String getTele_notifyAudioStatusUrl() {
		return tele_notifyAudioStatusUrl;
	}
	public String getTele_dmpRecordUrl() {
		return tele_dmpRecordUrl;
	}
	
	public String getSystemConfig() {
		return systemConfig;
	}
	public String getSystemConfig_id() {
		return systemConfig_id;
	}
	public String getSystemConfig_name() {
		return systemConfig_name;
	}
	public String getSystemConfig_details() {
		return systemConfig_details;
	}
	public String getSystemConfig_value() {
		return systemConfig_value;
	}
	public String getUser_endTime() {
		return user_endTime;
	}
	public String getSysPermission_parentId() {
		return sysPermission_parentId;
	}
	public String getComments() {
		return comments;
	}
	public String getComments_id() {
		return comments_id;
	}
	public String getComments_userId() {
		return comments_userId;
	}
	public String getComments_content() {
		return comments_content;
	}
	public String getComments_mobile() {
		return comments_mobile;
	}
	public String getComments_createTime() {
		return comments_createTime;
	}
	public String getComments_updateTime() {
		return comments_updateTime;
	}
	public String getComments_missCount() {
		return comments_missCount;
	}
	public String getComments_receivedId() {
		return comments_receivedId;
	}
	public String getUserNumber_currStatus() {
		return userNumber_currStatus;
	}
	public String getMessageRecord_messageType() {
		return messageRecord_messageType;
	}
	public String getSysOperatorLog() {
		return sysOperatorLog;
	}
	public String getSysOperatorLog_id() {
		return sysOperatorLog_id;
	}
	public String getSysOperatorLog_operatorId() {
		return sysOperatorLog_operatorId;
	}
	public String getSysOperatorLog_createTime() {
		return sysOperatorLog_createTime;
	}
	public String getSysOperatorLog_active() {
		return sysOperatorLog_active;
	}
	public String getSysOperatorLog_isDelete() {
		return sysOperatorLog_isDelete;
	}
	public String getNumbers_flag() {
		return numbers_flag;
	}
	public String getNumbers_callChannel() {
		return numbers_callChannel;
	}
	public String getNumbers_smsChannel() {
		return numbers_smsChannel;
	}
	public String getNumbers_sort() {
		return numbers_sort;
	}
	public String getUser_fromType() {
		return user_fromType;
	}
	public String getNumbers_weight() {
		return numbers_weight;
	}
	public String getNumbers_effect() {
		return numbers_effect;
	}
	public String getNumbers_type() {
		return numbers_type;
	}
	public String getNumbers_createTime() {
		return numbers_createTime;
	}

	public String getNumbers_inCache() {
		return numbers_inCache;
	}
	public String getSysOperator_status() {
		return sysOperator_status;
	}
	public String getCallRecord() {
		return callRecord;
	}
	public String getCallRecord_id() {
		return callRecord_id;
	}
	public String getCallRecord_userId() {
		return callRecord_userId;
	}
	public String getCallRecord_fromNumber() {
		return callRecord_fromNumber;
	}
	public String getCallRecord_toNumber() {
		return callRecord_toNumber;
	}
	public String getCallRecord_status() {
		return callRecord_status;
	}
	public String getCallRecord_startTime() {
		return callRecord_startTime;
	}
	public String getCallRecord_endTime() {
		return callRecord_endTime;
	}
	public String getCallRecord_isDelete() {
		return callRecord_isDelete;
	}
	public String getCallRecord_callMinutes() {
		return callRecord_callMinutes;
	}
	public String getCallRecord_errorMsg() {
		return callRecord_errorMsg;
	}
	public String getChargeRecord() {
		return chargeRecord;
	}
	public String getChargeRecord_id() {
		return chargeRecord_id;
	}
	public String getChargeRecord_userId() {
		return chargeRecord_userId;
	}
	public String getChargeRecord_payAccount() {
		return chargeRecord_payAccount;
	}
	public String getChargeRecord_mobile() {
		return chargeRecord_mobile;
	}
	public String getChargeRecord_status() {
		return chargeRecord_status;
	}
	public String getChargeRecord_type() {
		return chargeRecord_type;
	}
	public String getChargeRecord_createTime() {
		return chargeRecord_createTime;
	}
	public String getChargeRecord_price() {
		return chargeRecord_price;
	}
	public String getChargeRecord_openId() {
		return chargeRecord_openId;
	}
	public String getChargeRecord_updateTime() {
		return chargeRecord_updateTime;
	}
	public String getChargeRecord_errorMsg() {
		return chargeRecord_errorMsg;
	}
	public String getChargeRecord_serviceIds() {
		return chargeRecord_serviceIds;
	}
	public String getChargeRecord_isDelete() {
		return chargeRecord_isDelete;
	}
	public String getMessageRecord() {
		return messageRecord;
	}
	public String getMessageRecord_id() {
		return messageRecord_id;
	}
	public String getMessageRecord_userId() {
		return messageRecord_userId;
	}
	public String getMessageRecord_fromNumber() {
		return messageRecord_fromNumber;
	}
	public String getMessageRecord_toNumber() {
		return messageRecord_toNumber;
	}
	public String getMessageRecord_content() {
		return messageRecord_content;
	}
	public String getMessageRecord_createTime() {
		return messageRecord_createTime;
	}
	public String getMessageRecord_updateTime() {
		return messageRecord_updateTime;
	}
	public String getMessageRecord_sendStatus() {
		return messageRecord_sendStatus;
	}
	public String getMessageRecord_receiveStatus() {
		return messageRecord_receiveStatus;
	}
	public String getMessageRecord_isDelete() {
		return messageRecord_isDelete;
	}
	public String getMessageRecord_errorMsg() {
		return messageRecord_errorMsg;
	}
	public String getNumbers() {
		return numbers;
	}
	public String getNumbers_id() {
		return numbers_id;
	}
	public String getNumbers_mobile() {
		return numbers_mobile;
	}
	public String getNumbers_status() {
		return numbers_status;
	}
	public String getNumbers_operator() {
		return numbers_operator;
	}
	public String getNumbers_area() {
		return numbers_area;
	}
	public String getServices() {
		return services;
	}
	public String getServices_id() {
		return services_id;
	}
	public String getServices_name() {
		return services_name;
	}
	public String getServices_price() {
		return services_price;
	}
	public String getServices_type() {
		return services_type;
	}
	public String getServices_status() {
		return services_status;
	}
	public String getServices_createTime() {
		return services_createTime;
	}
	public String getServices_startTime() {
		return services_startTime;
	}
	public String getServices_endTime() {
		return services_endTime;
	}
	public String getServices_maxMsgCount() {
		return services_maxMsgCount;
	}
	public String getServices_maxCallMinutes() {
		return services_maxCallMinutes;
	}
	public String getServices_personIssuedCount() {
		return services_personIssuedCount;
	}
	public String getServices_personIssuedFlag() {
		return services_personIssuedFlag;
	}
	public String getServices_platformFlag() {
		return services_platformFlag;
	}
	public String getServices_delayDays() {
		return services_delayDays;
	}
	public String getServices_delayMinutes() {
		return services_delayMinutes;
	}
	public String getServices_mobilePool() {
		return services_mobilePool;
	}
	public String getUser() {
		return user;
	}
	public String getUser_id() {
		return user_id;
	}
	public String getUser_loginName() {
		return user_loginName;
	}
	public String getUser_countNumber() {
		return user_countNumber;
	}
	public String getUser_password() {
		return user_password;
	}
	public String getUser_mail() {
		return user_mail;
	}
	public String getUser_openId() {
		return user_openId;
	}
	public String getUser_status() {
		return user_status;
	}
	public String getUser_createTime() {
		return user_createTime;
	}
	public String getUser_updateTime() {
		return user_updateTime;
	}
	public String getUser_lastLoginTime() {
		return user_lastLoginTime;
	}
	public String getUser_sendSecretTime() {
		return user_sendSecretTime;
	}
	public String getUser_mailSecret() {
		return user_mailSecret;
	}
	public String getUser_sendDelayMsgFlag() {
		return user_sendDelayMsgFlag;
	}
	public String getUserLog() {
		return userLog;
	}
	public String getUserLog_id() {
		return userLog_id;
	}
	public String getUserLog_userId() {
		return userLog_userId;
	}
	public String getUserLog_userName() {
		return userLog_userName;
	}
	public String getUserLog_createTime() {
		return userLog_createTime;
	}
	public String getUserLog_active() {
		return userLog_active;
	}
	public String getUserLog_isDelete() {
		return userLog_isDelete;
	}
	public String getUserLog_deleteDes() {
		return userLog_deleteDes;
	}
	public String getNumbers_spId() {
		return numbers_spId;
	}
	public String getUserLog_activeDes() {
		return userLog_activeDes;
	}
	public String getUserNumber() {
		return userNumber;
	}
	public String getUserNumber_id() {
		return userNumber_id;
	}
	public String getUserNumber_userId() {
		return userNumber_userId;
	}
	public String getUserNumber_mobile() {
		return userNumber_mobile;
	}
	public String getUserNumber_redirectMobile() {
		return userNumber_redirectMobile;
	}
	public String getUserNumber_status() {
		return userNumber_status;
	}
	public String getUserNumber_createTime() {
		return userNumber_createTime;
	}
	public String getUserNumber_endTime() {
		return userNumber_endTime;
	}
	public String getUserNumber_bootTime() {
		return userNumber_bootTime;
	}
	public String getUserNumber_shutTime() {
		return userNumber_shutTime;
	}
	public String getUserNumber_isDelete() {
		return userNumber_isDelete;
	}
	public String getUserNumber_msgStartTime() {
		return userNumber_msgStartTime;
	}
	public String getUserNumber_msgEndTime() {
		return userNumber_msgEndTime;
	}
	public String getUserNumber_isRedirect() {
		return userNumber_isRedirect;
	}
	public String getUserServices() {
		return userServices;
	}
	public String getUserServices_id() {
		return userServices_id;
	}
	public String getUserServices_userId() {
		return userServices_userId;
	}
	public String getUserServices_serviceId() {
		return userServices_serviceId;
	}
	public String getUserServices_createTime() {
		return userServices_createTime;
	}
	public String getUserServices_currentMsgCount() {
		return userServices_currentMsgCount;
	}
	public String getUserServices_currentCallMinutes() {
		return userServices_currentCallMinutes;
	}
	public String getUserServices_weight() {
		return userServices_weight;
	}
	public String getUserServices_isDelete() {
		return userServices_isDelete;
	}
	
	public String getUserServices_status() {
		return userServices_status;
	}
	public String getSysOperator() {
		return sysOperator;
	}
	public String getSysOperator_id() {
		return sysOperator_id;
	}
	public String getSysOperator_login() {
		return sysOperator_login;
	}
	public String getSysOperator_password() {
		return sysOperator_password;
	}
	public String getSysOperator_name() {
		return sysOperator_name;
	}
	public String getSysOperator_telephone() {
		return sysOperator_telephone;
	}
	public String getSysOperator_createTime() {
		return sysOperator_createTime;
	}
	public String getSysOperator_updateTime() {
		return sysOperator_updateTime;
	}
	public String getSysOperator_lastLoginTime() {
		return sysOperator_lastLoginTime;
	}
	public String getSysOperatorRole() {
		return sysOperatorRole;
	}
	public String getSysOperatorRole_id() {
		return sysOperatorRole_id;
	}
	public String getSysOperatorRole_roleId() {
		return sysOperatorRole_roleId;
	}
	public String getSysOperatorRole_operatorId() {
		return sysOperatorRole_operatorId;
	}
	public String getSysRole() {
		return sysRole;
	}
	public String getSysRole_id() {
		return sysRole_id;
	}
	public String getSysRole_name() {
		return sysRole_name;
	}
	public String getSysRole_details() {
		return sysRole_details;
	}
	public String getSysRolePermission() {
		return sysRolePermission;
	}
	public String getSysRolePermission_id() {
		return sysRolePermission_id;
	}
	public String getSysRolePermission_roleId() {
		return sysRolePermission_roleId;
	}
	public String getSysRolePermission_permissionId() {
		return sysRolePermission_permissionId;
	}
	public String getSysPermission() {
		return sysPermission;
	}
	public String getSysPermission_id() {
		return sysPermission_id;
	}
	public String getSysPermission_name() {
		return sysPermission_name;
	}
	public String getSysPermission_path() {
		return sysPermission_path;
	}
	public String getSysPermission_methods() {
		return sysPermission_methods;
	}
	public String getSysPermission_menu() {
		return sysPermission_menu;
	}
	public String getStationLetter_id() {
		return stationLetter_id;
	}
	public String getStationLetter_createTime() {
		return stationLetter_createTime;
	}
	public String getStationLetter_content() {
		return stationLetter_content;
	}
	public String getStationLetter_type() {
		return stationLetter_type;
	}
	public String getStationLetter_status() {
		return stationLetter_status;
	}
	public String getStationLetter_fromUserId() {
		return stationLetter_fromUserId;
	}
	public String getStationLetter_fromNumber() {
		return stationLetter_fromNumber;
	}
	public String getStationLetter_toUserId() {
		return stationLetter_toUserId;
	}
	public String getStationLetter_toNumber() {
		return stationLetter_toNumber;
	}
	public String getStationLetter_latestId() {
		return stationLetter_latestId;
	}
	public String getCodes() {
		return codes;
	}
	public String getCodes_id() {
		return codes_id;
	}
	public String getCodes_codes() {
		return codes_codes;
	}
	public String getCodes_status() {
		return codes_status;
	}
	public String getCodes_serviceId() {
		return codes_serviceId;
	}
	public String getCodes_userId() {
		return codes_userId;
	}
	public String getCodes_useTime() {
		return codes_useTime;
	}
	public String getCodes_createTime() {
		return codes_createTime;
	}
	public String getCodes_sellerId() {
		return codes_sellerId;
	}
	public String getCodes_endTime() {
		return codes_endTime;
	}
	public String getCodes_isDelete() {
		return codes_isDelete;
	}
	public String getCodes_codesDes() {
		return codes_codesDes;
	}
	public String getTele() {
		return tele;
	}
	public String getTele_id() {
		return tele_id;
	}
	public String getTele_name() {
		return tele_name;
	}
	public String getTele_key() {
		return tele_key;
	}
	public String getTele_createTime() {
		return tele_createTime;
	}
	public String getTele_updateTime() {
		return tele_updateTime;
	}
	public String getTele_status() {
		return tele_status;
	}
	public String getTele_notifyCallUrl() {
		return tele_notifyCallUrl;
	}
	public String getTele_notifyMtReceUrl() {
		return tele_notifyMtReceUrl;
	}
	public String getTele_notifyMoUrl() {
		return tele_notifyMoUrl;
	}
	public String getTele_isDelete() {
		return tele_isDelete;
	}
	public String getCallRecord_vm() {
		return callRecord_vm;
	}
	
	
	public String getRelation(){
		return relation;
	}
	public String getRelation_id(){
		return relation_id;
	}
	public String getRelation_spId(){
		return relation_spId;
	}
	public String getRelation_bindMobile(){
		return relation_bindMobile;
	}
	public String getRelation_virtualMobile(){
		return relation_virtualMobile;
	}
	public String getRelation_status(){
		return relation_status;
	}
	public String getRelation_createTime(){
		return relation_createTime;
	}
	public String getRelation_updateTime(){
		return relation_updateTime;
	}
	public String getRelation_isDelete(){
		return relation_isDelete;
	}
	public String getRelation_onOff(){
		return relation_onOff;
	}
	
}
