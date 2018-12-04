package com.bootdo.system.controller;

import com.bootdo.common.annotation.Log;
import com.bootdo.common.config.Constant;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.FmenuDO;
import com.bootdo.system.service.FmenuService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author bootdo 1992lcg@163.com
 */
@RequestMapping("/sys/fmenu")
@Controller
public class FmenuController extends BaseController {
	String prefix = "system/fmenu";
	@Autowired
	FmenuService menuService;

	@RequiresPermissions("sys:fmenu:menu")
	@GetMapping()
	String menu(Model model) {
		return prefix+"/menu";
	}

	@RequiresPermissions("sys:fmenu:menu")
	@RequestMapping("/list")
	@ResponseBody
	List<FmenuDO> list(@RequestParam Map<String, Object> params) {
		List<FmenuDO> menus = menuService.list(params);
		return menus;
	}

	@Log("添加菜单")
	@RequiresPermissions("sys:fmenu:add")
	@GetMapping("/add/{pId}")
	String add(Model model, @PathVariable("pId") Long pId) {
		model.addAttribute("pId", pId);
		if (pId == 0) {
			model.addAttribute("pName", "根目录");
		} else {
			model.addAttribute("pName", menuService.get(pId).getName());
		}
		return prefix + "/add";
	}

	@Log("编辑菜单")
	@RequiresPermissions("sys:fmenu:edit")
	@GetMapping("/edit/{id}")
	String edit(Model model, @PathVariable("id") Long id) {
		FmenuDO mdo = menuService.get(id);
		Long pId = mdo.getParentId();
		model.addAttribute("pId", pId);
		if (pId == 0) {
			model.addAttribute("pName", "根目录");
		} else {
			model.addAttribute("pName", menuService.get(pId).getName());
		}
		model.addAttribute("menu", mdo);
		return prefix+"/edit";
	}

	@Log("保存菜单")
	@RequiresPermissions("sys:fmenu:add")
	@PostMapping("/save")
	@ResponseBody
	R save(FmenuDO menu) {
		if (Constant.DEMO_ACCOUNT.equals(getUsername())) {
			return R.error(1, "演示系统不允许修改,完整体验请部署程序");
		}
		if (menuService.save(menu) > 0) {
			return R.ok();
		} else {
			return R.error(1, "保存失败");
		}
	}

	@Log("更新菜单")
	@RequiresPermissions("sys:fmenu:edit")
	@PostMapping("/update")
	@ResponseBody
	R update(FmenuDO menu) {
		if (Constant.DEMO_ACCOUNT.equals(getUsername())) {
			return R.error(1, "演示系统不允许修改,完整体验请部署程序");
		}
		if (menuService.update(menu) > 0) {
			return R.ok();
		} else {
			return R.error(1, "更新失败");
		}
	}

	@Log("删除菜单")
	@RequiresPermissions("sys:fmenu:remove")
	@PostMapping("/remove")
	@ResponseBody
	R remove(Long id) {
		if (Constant.DEMO_ACCOUNT.equals(getUsername())) {
			return R.error(1, "演示系统不允许修改,完整体验请部署程序");
		}
		if (menuService.remove(id) > 0) {
			return R.ok();
		} else {
			return R.error(1, "删除失败");
		}
	}

	@GetMapping("/tree")
	@ResponseBody
	Tree<FmenuDO> tree() {
		Tree<FmenuDO>  tree = menuService.getTree();
		return tree;
	}

	@GetMapping("/tree/{roleId}")
	@ResponseBody
	Tree<FmenuDO> tree(@PathVariable("roleId") Long roleId) {
		Tree<FmenuDO> tree = menuService.getTree(roleId);
		return tree;
	}
}
