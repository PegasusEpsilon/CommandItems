package com.pegasusepsilon.commanditems.ItemEvent;

//import com.pegasusepsilon.commanditems.Core;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.MouseEvent;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;

import org.apache.logging.log4j.Logger;

public class ItemEvent {
	@SubscribeEvent
	public static void mouseEvent (MouseEvent event) {
//		Core.logger.info("Mouse Event");
	}
}
