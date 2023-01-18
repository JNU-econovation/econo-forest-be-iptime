package com.example.iptimeAPI.domain.iptime;

import com.example.iptimeAPI.domain.macAddress.MacAddress;
import com.example.iptimeAPI.service.iptime.dto.IpResponseDTO;
import com.example.iptimeAPI.web.dto.IpDTO;

import java.util.List;

public interface IptimeService {

    IpResponseDTO isInIptime(IpDTO ipDTO);

    void isExistMacAddress(String macAddress);

    List<Long> browseExistMembers(List<MacAddress.MacAddressResponseDTO> registeredMacAddresses );
}
