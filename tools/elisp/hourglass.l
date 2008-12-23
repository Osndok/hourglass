;
; Hourglass - a time tracking utility.
; Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
;
; This program is free software; you can redistribute it and/or modify
; it under the terms of the GNU General Public License as published by
; the Free Software Foundation; either version 2 of the License, or
; (at your option) any later version.
;
; This program is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU General Public License for more details.
;
; You should have received a copy of the GNU General Public License
; along with this program; if not, write to the Free Software
; Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
;
; --------------------------------------------------------------------
;
; CVS Revision $Revision: 1.2 $
; Last modified on $Date: 2003/10/25 05:14:57 $ by $Author: mgrant79 $
;

;
; Emacs macros for Hourglass
; ====================================================================



;
; "Private" functions.  Use the convention of prefixing private
; functions with a "_"
; --------------------------------------------------------------------

(defun _HG-get-email-address() 
  (if (getenv "EMAIL")
      (getenv "EMAIL")
    (concat (user-login-name) "@"
            (if (getenv "HOSTNAME")
                (getenv "HOSTNAME")
              "localhost"))))

(defun _HG-get-full-name()
  (if (getenv "FULLNAME")
      (getenv "FULLNAME")
    (user-full-name)))

;
; "Public" functions
; --------------------------------------------------------------------



;
; Makes a Java source file header at the top of the current buffer.
;
; If you set the EMAIL environment variable, it will be used as the
; email address, otherwise something like "username@localhost" will be
; substituted.
;
(defun HG-make-java-header()
  "Makes a Java source file header at the top of the current buffer."
  (interactive)
  (beginning-of-buffer)
  (let ((name_string (concat (_HG-get-full-name) " <" (_HG-get-email-address) ">")))
    (insert "/*")(newline)
    (insert " * Hourglass - a time tracking utility.")(newline)
    
    (insert " * Copyright (C) ")
    (insert (format-time-string "%Y"))(insert " ")
    (insert name_string)(newline)
    
    (insert " *")(newline)
    (insert " * This program is free software; you can redistribute it and/or modify")(newline)
    (insert " * it under the terms of the GNU General Public License as published by")(newline)
    (insert " * the Free Software Foundation; either version 2 of the License, or")(newline)
    (insert " * (at your option) any later version.")(newline)
    (insert " *")(newline)
    (insert " * This program is distributed in the hope that it will be useful,")(newline)
    (insert " * but WITHOUT ANY WARRANTY; without even the implied warranty of")(newline)
    (insert " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the")(newline)
    (insert " * GNU General Public License for more details.")(newline)
    (insert " *")(newline)
    (insert " * You should have received a copy of the GNU General Public License")(newline)
    (insert " * along with this program; if not, write to the Free Software")(newline)
    (insert " * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA")(newline)
    (insert " *")(newline)
    (insert " * --------------------------------------------------------------------")(newline)
    (insert " *")(newline)
    (insert " * CVS Revision $Revision: 1.2 $")(newline)
    (insert " * Last modified on $Date: 2003/10/25 05:14:57 $ by $Author: mgrant79 $")(newline)
    (insert " *")(newline)
    (insert " */")(newline)
    (insert "package net.sourceforge.hourglass.---;")(newline)
    (newline)
    (insert "/**")(newline)
    (insert " *")(newline)
    (insert " * @author ")(insert name_string)(newline)
    (insert " */")(newline)
    (insert "public class --- {")(newline)
    (newline)
    (insert "}")(newline)))