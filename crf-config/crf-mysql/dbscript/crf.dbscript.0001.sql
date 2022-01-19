USE crf;

UPDATE `crf`.`email_templates` SET `message` = '<table width=\"600\" cellspacing=\"0\" border=\"0\" cellpadding=\"0\" style=\"width:100%;padding:32px 50px 32px;border-left:1px solid #e8e8e9;border-right:1px solid #e8e8e9\"><tbody><tr><td><p style=\"font-family:\'Helvetica Neue\';font-size:26px;font-weight:normal;color:#545457;margin:1em 0\">Hello ${firstName},</p></td></tr><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"width:100%\"><tbody><tr><td><p style=\"font-family:Helvetica;font-size:16px;color:#545457;line-height:22px;margin-bottom:1.3em\">We are delighted to confirm your registration with the JMGCFinance Platform! Please note, a photo of your ID and a photo clearly showing your face will be required by the platform for identification. Please click on the link below.</p></td></tr><tr><td><p style=\"font-family:Helvetica;font-size:16px;color:#545457;line-height:22px;margin-bottom:1.3em\">Please <a target=\"_blank\" href=\"${loginPageUrl}\"><strong>click here</strong></a> to login and upload your photos.<br></p></td></tr><tr><td><p style=\"font-family:Helvetica;font-size:16px;color:#707074;line-height:22px;margin-bottom:1em\">\nKind Regards,<br><strong>JMGCFinance Support Team</strong><br></p></td></tr></tbody></table></td></tr></tbody></table>' WHERE (`id` = '3');