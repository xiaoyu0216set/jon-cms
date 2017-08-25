package org.jon.lv.controller;

import com.alibaba.fastjson.JSONObject;
import org.jon.lv.annotation.UnLoginLimit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Controller
@RequestMapping(value = "admin")
public class AdminController {

	public final static String SESSION_KEYCODE = "SESSION_KEYCODE";//验证码

	Font mFont = new Font("Times New Roman", Font.BOLD, 17);

	private static Color getRandColor(int fc, int bc) {
		Random random = new Random();
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * 验证码
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
     */
	@UnLoginLimit
	@GetMapping("/captcha")
	public void getVerCode(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		int width = 100, height = 33;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();
		Random random = new Random();
		g.setColor(getRandColor(200, 250));
		g.fillRect(1, 1, width - 1, height - 1);
		g.setColor(new Color(102, 102, 102));
		g.drawRect(0, 0, width - 1, height - 1);
		g.setFont(this.mFont);

		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width - 1);
			int y = random.nextInt(height - 1);
			int xl = random.nextInt(6) + 1;
			int yl = random.nextInt(12) + 1;
			g.drawLine(x, y, x + xl, y + yl);
		}
		for (int i = 0; i < 70; i++) {
			int x = random.nextInt(width - 1);
			int y = random.nextInt(height - 1);
			int xl = random.nextInt(12) + 1;
			int yl = random.nextInt(6) + 1;
			g.drawLine(x, y, x - xl, y - yl);
		}
		StringBuffer sRand = new StringBuffer();
		String ch;
		//数字字母
		String baseNum = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < 6; i++) {
			ch = baseNum.charAt(new Random().nextInt(baseNum.length())) + "";
			sRand.append(ch);

			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(String.valueOf(ch), 16 * i + 10, 25);
		}
		request.getSession().setAttribute(SESSION_KEYCODE, sRand);
		g.dispose();
		ImageIO.write(image, "JPEG", response.getOutputStream());
	}

	/**
	 * 登录跳转
	 * @param model
	 * @return
	 */
	@UnLoginLimit
	@GetMapping("/login")
	public String loginGet(Model model) {
		return "login";
	}

	/**
	 * 登录
	 * @return
	 */
	@UnLoginLimit
	@PostMapping("/enter")
	public String enter(String userName, String password, String captcha,
							Model model, HttpSession session) {

		String code = String.valueOf(session.getAttribute(SESSION_KEYCODE));

		if ("jon".equals(userName) && "123456".equals(password)
				&& code.equalsIgnoreCase(captcha)) {
			JSONObject object = new JSONObject();
			object.put("userName", userName);
			object.put("password", password);
			session.setAttribute("admin", object);
			return "redirect:dashboard";
		} else {
			model.addAttribute("error", "用户名或密码错误，请重新登录！");
			return "login";
		}
	}

	/**
	 * 注册
	 * 
	 * @param model
	 * @return
	 */
	@UnLoginLimit
	@GetMapping("/register")
	public String register(Model model) {
		return "register";
	}


	/**
	 * 退出登录
	 * @return
     */
	@GetMapping("/exit")
	public String exit(HttpSession session) {

		session.removeAttribute("admin");

		return "login";
	}

	/**
	 * 仪表板页面
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		return "dashboard";
	}
}