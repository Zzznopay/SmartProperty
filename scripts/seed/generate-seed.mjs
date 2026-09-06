#!/usr/bin/env node
/**
 * SmartProperty 演示种子数据生成器
 *
 * 生成一个住宅小区（云栖湾，6 栋 11 层 2 单元 264 户）量级的业务数据，
 * 覆盖 系统基础 / 房产财务 / 运营管理 三个库，数据间保持业务勾稽关系：
 *   房间归属楼宇/单元/小区，业主-房间 1:1/N:1 关联，销售/租赁合同与房间状态一致，
 *   台账金额 = 建筑面积 × 物业费单价，收缴记录与台账状态/金额对应，
 *   抄表读数递增且用量 = 本次 - 上次，车辆进出有来有回等。
 *
 * 敏感字段（手机号/身份证）使用与 EncryptUtils 相同的 AES-128-ECB/PKCS5 算法
 * （密钥 = encrypt.key 配置的明文）离线加密后写入，脱敏列同步生成。
 *
 * 用法: node scripts/seed/generate-seed.mjs
 * 输出: deploy/sql/seed/smart_property_{system,property,operation}-seed.sql
 */
import { createCipheriv } from 'node:crypto';
import { mkdirSync, writeFileSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = join(dirname(fileURLToPath(import.meta.url)), '..', '..');
const OUT_DIR = join(ROOT, 'deploy', 'sql', 'seed');

// ---------- 基础工具 ----------
const ENCRYPT_KEY = '1234567890abcdef'; // application-common.yml: encrypt.key 解密后的明文（本地开发默认）
function aes(plain) {
  const c = createCipheriv('aes-128-ecb', Buffer.from(ENCRYPT_KEY), null);
  return Buffer.concat([c.update(plain, 'utf8'), c.final()]).toString('base64');
}
function mulberry32(seed) {
  let a = seed >>> 0;
  return function () {
    a |= 0; a = (a + 0x6d2b79f5) | 0;
    let t = Math.imul(a ^ (a >>> 15), 1 | a);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}
const rnd = mulberry32(20260906);
const pick = (arr) => arr[Math.floor(rnd() * arr.length)];
const rint = (min, max) => min + Math.floor(rnd() * (max - min + 1));
const money = (n) => Math.round(n * 100) / 100;
const q = (s) => (s == null ? 'NULL' : `'${String(s).replace(/'/g, "''")}'`);
const d = (s) => (s == null ? 'NULL' : `'${s}'`);
function pad(n, w) { return String(n).padStart(w, '0'); }

// 身份证校验码（GB 11643-1999）
function idCardChecksum(body17) {
  const w = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
  const codes = '10X98765432';
  let sum = 0;
  for (let i = 0; i < 17; i++) sum += Number(body17[i]) * w[i];
  return codes[sum % 11];
}

// ---------- 姓名池 ----------
const SURNAMES = '王李张刘陈杨黄赵吴周徐孙马朱胡郭何林罗高郑梁谢宋唐许韩冯邓曹彭肖田董袁蔡蒋叶程苏魏吕'.split('');
const GIVEN_CHARS = '志建国家明华文军平辉强伟东芳丽娟敏静丽婷玉萍红梅琳晶云莉兰鑫磊金海燕雪莉飞宇浩然泽晨旭昊天翔雅倩怡欣悦佳琪璇蕾楠松柏楠熙雅雯静怡涵博轩诚毅峰磊'.split('');
const usedNames = new Set();
function personName() {
  for (let i = 0; i < 500; i++) {
    const n = pick(SURNAMES) + (rnd() < 0.75 ? pick(GIVEN_CHARS) + pick(GIVEN_CHARS) : pick(GIVEN_CHARS));
    if (!usedNames.has(n)) { usedNames.add(n); return n; }
  }
  throw new Error('姓名池耗尽');
}

// ---------- 业务常量 ----------
const COMPANY_ID = 1;
const COMMUNITY_ID = 1;
const COMMUNITY_NAME = '云栖湾小区';
const COMMUNITY_CODE = 'HJY-YXW-001';
const COMMUNITY_ADDR = '杭州市西湖区文栖路 398 号';
const PROPERTY_FEE = 2.8; // 元/㎡/月
const STAFF = [
  { id: 2, username: 'zhangwm', name: '张伟民', dept: 1, role: 2, gender: 1, phone: '13900001002', title: '物业经理' },
  { id: 3, username: 'wangyf', name: '王雅芳', dept: 5, role: 3, gender: 2, phone: '13900001003', title: '财务主管' },
  { id: 4, username: 'lichen', name: '李晨', dept: 5, role: 3, gender: 2, phone: '13900001004', title: '会计' },
  { id: 5, username: 'liting', name: '李婷', dept: 4, role: 4, gender: 2, phone: '13900001005', title: '客服主管' },
  { id: 6, username: 'zhouqian', name: '周倩', dept: 4, role: 4, gender: 2, phone: '13900001006', title: '客服专员' },
  { id: 7, username: 'liuzg', name: '刘志刚', dept: 6, role: 5, gender: 1, phone: '13900001007', title: '维修工程师' },
  { id: 8, username: 'sunjj', name: '孙建军', dept: 6, role: 5, gender: 1, phone: '13900001008', title: '水电工' },
  { id: 9, username: 'zhaotj', name: '赵铁军', dept: 7, role: 6, gender: 1, phone: '13900001009', title: '秩序班长' },
];
const BCRYPT_HASH = '$2a$10$HudK2dk5PYP7CJ1UQBxfBOWyrnDrVtak304mExjvwwbk/OpLyJhKi'; // Aa123456

// 员工工号（保洁/保安/绿化，非系统用户）
const CLEANERS = [
  { id: 9001, name: '张秀兰' }, { id: 9002, name: '王秀英' }, { id: 9003, name: '李桂花' },
];
const GUARDS = [
  { id: 9011, name: '赵建国' }, { id: 9012, name: '钱进' }, { id: 9013, name: '孙立' }, { id: 9014, name: '吴卫国' },
];
const GREEN_WORKER = { id: 9021, name: '王保国' };

// ---------- 生成房产主体 ----------
const buildings = [];
for (let b = 1; b <= 6; b++) {
  buildings.push({
    id: b,
    communityId: COMMUNITY_ID,
    name: `${b} 号楼`,
    code: `YXW-B${pad(b, 2)}`,
    type: 1, floorCount: 11, roomCount: 44,
    area: money(11 * 4 * ((76.8 + 98.5 + 118.6 + 88.2) / 4)),
    buildYear: 2022,
  });
}
const units = [];
for (let b = 1; b <= 6; b++) {
  for (let u = 1; u <= 2; u++) {
    units.push({
      id: (b - 1) * 2 + u,
      buildingId: b,
      name: `${u} 单元`,
      code: `YXW-B${pad(b, 2)}-U${u}`,
      floorCount: 11, roomCount: 22, sort: u,
    });
  }
}
// 户型面积：每单元 2 户，4 种户型轮换
const AREA = [76.8, 98.5, 118.6, 88.2];
const rooms = [];
for (let b = 1; b <= 6; b++) {
  for (let u = 1; u <= 2; u++) {
    const unitId = (b - 1) * 2 + u;
    for (let f = 1; f <= 11; f++) {
      for (let s = 1; s <= 2; s++) {
        const idx = (u - 1) * 22 + (f - 1) * 2 + s; // 1..22 单元内序号
        const build = AREA[(idx - 1) % 4];
        const roomNo = `${f}${s === 1 ? '0' : ''}${s}`; // 101 102 ... 1102 → f*100+s (s<10)
        rooms.push({
          id: rooms.length + 1,
          communityId: COMMUNITY_ID,
          buildingId: b,
          unitId,
          roomCode: `YXW-B${pad(b, 2)}-U${u}-${f}${s === 1 ? '0' : ''}${s}`,
          roomNo: `${f * 100 + s}`,
          floor: f,
          roomType: 1,
          buildArea: build,
          innerArea: money(build * 0.8),
          publicArea: money(build * 0.2),
          orientation: s === 1 ? '南' : '北',
          decoration: 3,
          status: 1, ownerId: null, tenantId: null, checkInTime: null,
          unitLabel: `B${pad(b, 2)}-U${u}`,
        });
      }
    }
  }
}
// 房间状态分配：26 出租 / 6 装修 / 46 空置 / 其余已售入住
const shuffled = rooms.map((_, i) => i);
for (let i = shuffled.length - 1; i > 0; i--) {
  const j = Math.floor(rnd() * (i + 1));
  [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
}
const rentedIdx = shuffled.slice(0, 26);
const decoratingIdx = shuffled.slice(26, 32);
const vacantIdx = shuffled.slice(32, 78);
const vacantSet = new Set(vacantIdx);
const RENTED = new Set(rentedIdx);
const DECORATING = new Set(decoratingIdx);

// ---------- 业主 ----------
const owners = [];
const ownerRooms = []; // {ownerId, roomId}
const soldIdx = [...rentedIdx, ...decoratingIdx, ...rooms.map((_, i) => i).filter((i) =>
  !RENTED.has(i) && !DECORATING.has(i) && !vacantSet.has(i))];
// soldIdx 长度 = 26 + 6 + 186 = 218；业主 1..214 各 1 房，215/216 各 2 房
soldIdx.forEach((roomIdx, i) => {
  let ownerId;
  if (i < 214) ownerId = i + 1;
  else if (i === 214 || i === 215) ownerId = 215;
  else ownerId = 216;
  ownerRooms.push({ ownerId, roomId: rooms[roomIdx].id });
  rooms[roomIdx].ownerId = ownerId;
});
for (let i = 1; i <= 216; i++) {
  const gender = rnd() < 0.52 ? 1 : 2;
  const name = personName();
  const birthY = rint(1962, 2002);
  const birth = `${birthY}${pad(rint(1, 12), 2)}${pad(rint(1, 28), 2)}`;
  const seq = pad(rint(1, 499) * 2 + (gender === 1 ? 1 : 0), 3);
  const body = `330106${birth}${seq}`;
  const idCard = body + idCardChecksum(body);
  const phone = `1380000${pad(1000 + i, 4)}`;
  const myRooms = ownerRooms.filter((r) => r.ownerId === i);
  const mainRoom = rooms.find((r) => r.id === myRooms[0].roomId);
  owners.push({
    id: i,
    code: `YZ-${pad(i, 4)}`,
    name,
    gender,
    idCard,
    idCardEnc: aes(idCard),
    idCardMask: `330106********${idCard.slice(-4)}`,
    phone,
    phoneEnc: aes(phone),
    phoneMask: `138****${phone.slice(-4)}`,
    email: null,
    ownerType: 1,
    emergency: personName(),
    emergencyPhone: `1380000${pad(5000 + i, 4)}`,
    address: `${COMMUNITY_ADDR}${mainRoom.roomNo.replace(/^0/, '')}室`,
  });
}
// 房间状态/租户落地
rentedIdx.forEach((roomIdx, i) => {
  rooms[roomIdx].status = 3;
  rooms[roomIdx].tenantId = i + 1;
  rooms[roomIdx].checkInTime = '2026-03-01 10:00:00';
});
decoratingIdx.forEach((roomIdx) => { rooms[roomIdx].status = 4; });
rooms.forEach((r) => {
  if (!RENTED.has(r.id - 1) && !DECORATING.has(r.id - 1) && !vacantSet.has(r.id - 1)) {
    if (r.ownerId) {
      r.status = 2;
      r.checkInTime = `2023-0${rint(7, 9)}-${pad(rint(1, 28), 2)} 10:00:00`;
    }
  }
});

// ---------- 租户 & 租赁合同 ----------
const tenants = [];
for (let i = 1; i <= 28; i++) {
  const gender = rnd() < 0.5 ? 1 : 2;
  const name = personName();
  const birthY = rint(1975, 2000);
  const birth = `${birthY}${pad(rint(1, 12), 2)}${pad(rint(1, 28), 2)}`;
  const seq = pad(rint(1, 499) * 2 + (gender === 1 ? 1 : 0), 3);
  const idCard = `330108${birth}${seq}` + idCardChecksum(`330108${birth}${seq}`);
  const phone = `1390000${pad(1000 + i, 4)}`;
  tenants.push({
    id: i, code: `ZK-${pad(i, 3)}`, name, gender,
    idCard, idCardEnc: aes(idCard), idCardMask: `330108********${idCard.slice(-4)}`,
    phone, phoneEnc: aes(phone), phoneMask: `139****${phone.slice(-4)}`,
    company: i % 4 === 0 ? pick(['杭州云栖科技有限公司', '杭州梧桐会计师事务所', '浙江蓝湾文化传媒有限公司', '杭州青梧教育咨询有限公司']) : null,
  });
}
const leaseContracts = [];
rentedIdx.forEach((roomIdx, i) => {
  const room = rooms[roomIdx];
  const start = i < 16 ? '2026-03-01' : '2026-06-01';
  leaseContracts.push({
    id: i + 1,
    no: `ZL-2026-${pad(i + 1, 4)}`,
    roomId: room.id, tenantId: i + 1,
    leaseType: 1,
    startDate: start,
    endDate: i < 16 ? '2027-02-28' : '2027-05-31',
    rentAmount: money(room.buildArea * 32),
    deposit: money(room.buildArea * 32 * 2),
    payCycle: 1,
    status: 2,
  });
});
// 2 条已终止的历史合同（对应已售入住房间，租客搬走业主入住）
[27, 28].forEach((tid, i) => {
  const room = rooms[soldIdx[100 + i]];
  leaseContracts.push({
    id: 27 + i,
    no: `ZL-2025-${pad(90 + i, 4)}`,
    roomId: room.id, tenantId: tid,
    leaseType: 1,
    startDate: '2025-09-01', endDate: '2026-08-31',
    rentAmount: money(room.buildArea * 30),
    deposit: money(room.buildArea * 30 * 2),
    payCycle: 1,
    status: 3, terminateDate: '2026-08-15', terminateReason: '租约到期不再续租，业主收回自住',
  });
});

// ---------- 销售合同 / 验房 ----------
const saleContracts = [];
const checkRecords = [];
ownerRooms.forEach((or, i) => {
  const room = rooms.find((r) => r.id === or.roomId);
  const price = money(room.buildArea * 38500);
  const payType = i % 10 < 2 ? 1 : i % 10 < 9 ? 2 : 3;
  saleContracts.push({
    id: i + 1,
    no: `XS-2023-${pad(i + 1, 4)}`,
    roomId: room.id, ownerId: or.ownerId,
    contractDate: `2023-0${rint(3, 6)}-${pad(rint(1, 28), 2)}`,
    salePrice: price,
    payType,
    downPayment: payType === 1 ? price : money(price * 0.3),
    loanAmount: payType === 2 ? money(price * 0.7) : 0,
    deliveryDate: '2023-06-30', deliveryStatus: 1, status: 2,
  });
  const bad = i % 10 === 3;
  checkRecords.push({
    id: i + 1,
    roomId: room.id, ownerId: or.ownerId,
    checkType: 2, checkDate: `2023-07-${pad(rint(1, 28), 2)}`,
    checkResult: bad ? 2 : 1,
    problems: bad ? pick(['客厅墙面空鼓 2 处', '主卧窗户密封条缺失', '卫生间地漏排水不畅', '入户门闭合不严']) : null,
    status: i % 25 === 0 ? 1 : 2,
    remark: bad ? '物业已联系施工方整改' : '验房合格',
  });
});

// ---------- 装修记录（20 条：6 条装修中 + 14 条历史完工） ----------
const decorationRecords = [];
{
  const decRooms = [...decoratingIdx.map((i) => rooms[i])];
  const extra = rooms.filter((r) => r.status === 2).slice(0, 14);
  [...decRooms, ...extra].forEach((room, i) => {
    const ongoing = i < 6;
    decorationRecords.push({
      id: i + 1,
      roomId: room.id, ownerId: room.ownerId,
      applyDate: ongoing ? `2026-08-${pad(rint(10, 25), 2)}` : `2023-0${rint(8, 11)}-${pad(rint(1, 28), 2)}`,
      startDate: ongoing ? `2026-09-0${rint(1, 5)}` : `2023-${pad(rint(9, 11), 2)}-${pad(rint(1, 28), 2)}`,
      endDate: ongoing ? '2026-11-30' : `2024-0${rint(1, 3)}-${pad(rint(1, 28), 2)}`,
      company: pick(['杭州鸿运装饰工程有限公司', '杭州铭匠装饰设计有限公司', '杭州居美家装饰有限公司']),
      contact: personName(),
      contactPhone: `1370000${pad(2000 + i, 4)}`,
      deposit: 3000,
      depositStatus: ongoing ? 2 : 3,
      checkResult: ongoing ? null : 1,
      status: ongoing ? 2 : 4,
      remark: ongoing ? '装修期间已交纳押金，物业每日巡查' : '装修验收合格，押金已退',
    });
  });
}

// ---------- 费项 / 阶梯 ----------
const feeItems = [
  { id: 1, name: '物业管理费', code: 'YXW-WYF', type: 1, mode: 1, price: 2.8, unit: '元/㎡·月', cycle: 1, ladder: 0 },
  { id: 2, name: '公摊电费', code: 'YXW-GYD', type: 2, mode: 2, price: 24, unit: '元/户·月', cycle: 1, ladder: 0 },
  { id: 3, name: '生活水费', code: 'YXW-SSF', type: 1, mode: 3, price: 3.1, unit: '元/吨', cycle: 1, ladder: 1 },
  { id: 4, name: '生活电费', code: 'YXW-SDF', type: 1, mode: 3, price: 0.588, unit: '元/度', cycle: 1, ladder: 1 },
  { id: 5, name: '装修垃圾清运费', code: 'YXW-ZXLF', type: 3, mode: 2, price: 400, unit: '元/户', cycle: 4, ladder: 0 },
];
const ladderConfigs = [
  { id: 1, feeItemId: 3, name: '第一阶梯', min: 0, max: 216, price: 3.1, sort: 1 },
  { id: 2, feeItemId: 3, name: '第二阶梯', min: 216, max: 300, price: 4.0, sort: 2 },
  { id: 3, feeItemId: 3, name: '第三阶梯', min: 300, max: null, price: 5.0, sort: 3 },
  { id: 4, feeItemId: 4, name: '第一阶梯', min: 0, max: 2760, price: 0.588, sort: 1 },
  { id: 5, feeItemId: 4, name: '第二阶梯', min: 2760, max: 4800, price: 0.668, sort: 2 },
  { id: 6, feeItemId: 4, name: '第三阶梯', min: 4800, max: null, price: 0.928, sort: 3 },
];

// ---------- 台账 / 收费（物业管理费 2026-06/07/08） ----------
const MONTHS = ['2026-06', '2026-07', '2026-08'];
const ledgers = [];
const payments = [];
const paymentDetails = [];
const unpaid07 = new Set();
{
  const soldRooms = ownerRooms.map((or) => rooms.find((r) => r.id === or.roomId));
  const shuffledSold = soldRooms.map((r) => r.id);
  for (let i = shuffledSold.length - 1; i > 0; i--) {
    const j = Math.floor(rnd() * (i + 1));
    [shuffledSold[i], shuffledSold[j]] = [shuffledSold[j], shuffledSold[i]];
  }
  shuffledSold.slice(0, 26).forEach((id) => unpaid07.add(id));
  const paid08Set = new Set(shuffledSold.slice(26, 26 + 120));
  const partial08Set = new Set(shuffledSold.slice(26 + 120, 26 + 120 + 17));

  let paySeq = 0;
  let ledgerId = 0;
  for (const m of MONTHS) {
    for (const room of soldRooms) {
      const amount = money(room.buildArea * PROPERTY_FEE);
      const isUnpaid07 = m === '2026-07' && unpaid07.has(room.id);
      const paidFull08 = m === '2026-08' && paid08Set.has(room.id);
      const isPartial08 = m === '2026-08' && partial08Set.has(room.id);
      let status = 3, paid = amount, payTime = `${m}-18 14:${pad(rint(10, 50), 2)}:00`;
      if (m === '2026-08') {
        if (paidFull08) { /* full */ }
        else if (isPartial08) { status = 2; paid = money(amount * 0.6); payTime = `${m}-20 09:30:00`; }
        else { status = 1; paid = 0; payTime = null; }
      } else if (isUnpaid07) { status = 1; paid = 0; payTime = null; }
      const discount = status !== 1 && rnd() < 0.02 ? money(amount * 0.05) : 0;
      if (discount > 0 && status === 3) paid = money(amount - discount);
      ledgers.push({
        id: ++ledgerId,
        roomId: room.id, ownerId: room.ownerId,
        feeItemId: 1, month: m, amount,
        paid: status === 1 ? 0 : paid,
        discount, lateFee: 0, status,
        dueDate: `${m}-25`, payTime,
        remark: null,
      });
      if (status !== 1) {
        paySeq++;
        payments.push({
          id: payments.length + 1,
          no: `SK-${m.replace('-', '')}-${pad(paySeq, 4)}`,
          roomId: room.id, ownerId: room.ownerId,
          total: amount, actual: paid, discount,
          payType: pick([3, 3, 3, 2, 2, 1, 4]),
          payTime,
          receipt: `SJ-${m.replace('-', '')}-${pad(paySeq, 4)}`,
          invoiceNo: null,
          cashier: pick([staff(3), staff(4)]),
          status: 1, audit: 1,
          remark: null,
          ledgerId: ledgerId,
        });
      }
    }
  }
  payments.forEach((p) => {
    const l = ledgers.find((x) => x.id === p.ledgerId);
    paymentDetails.push({
      id: paymentDetails.length + 1,
      paymentId: p.id, ledgerId: l.id, feeItemId: 1, month: l.month,
      amount: l.amount, actual: p.actual, discount: p.discount, lateFee: 0,
    });
  });
}
function staff(id) { return { id, name: STAFF.find((s) => s.id === id).name.trim() }; }

// ---------- 车位 ----------
const parkings = [];
for (let i = 1; i <= 120; i++) {
  const underground = i <= 100;
  let status = 1, ownerId = null, tenantId = null, salePrice = null, saleDate = null, rentPrice = null, rentStart = null, rentEnd = null;
  if (i <= 40) {
    status = 2; ownerId = i; salePrice = underground ? 150000 : 50000; saleDate = '2023-06-20';
  } else if (i <= 100) {
    status = 3; rentPrice = 350; rentStart = '2026-01-01'; rentEnd = '2026-12-31';
    if (i <= 80) ownerId = i; else tenantId = i - 80;
  } else if (i <= 110) {
    status = 3; rentPrice = 150; rentStart = '2026-04-01'; rentEnd = '2027-03-31';
    ownerId = i - 20;
  }
  parkings.push({
    id: i,
    no: underground ? `B1-${pad(i, 3)}` : `P-${pad(i - 100, 3)}`,
    type: underground ? 2 : 1,
    area: underground ? 32.5 : 12.5,
    ownerId, tenantId, status, salePrice, saleDate, rentPrice, rentStart, rentEnd,
  });
}
const parkingPayments = [];
{
  let seq = 0;
  for (const m of ['2026-07', '2026-08']) {
    for (const p of parkings) {
      if (p.status === 3) {
        seq++;
        parkingPayments.push({
          id: parkingPayments.length + 1,
          parkingId: p.id, no: `TK-${m.replace('-', '')}-${pad(seq, 4)}`,
          month: m, feeType: 2, amount: p.rentPrice, actual: p.rentPrice,
          payTime: `${m}-05 10:${pad(rint(10, 59), 2)}:00`, payType: 3, status: 1, remark: null,
        });
      } else if (p.status === 2) {
        seq++;
        parkingPayments.push({
          id: parkingPayments.length + 1,
          parkingId: p.id, no: `TK-${m.replace('-', '')}-${pad(seq, 4)}`,
          month: m, feeType: 1, amount: 60, actual: 60,
          payTime: `${m}-05 10:${pad(rint(10, 59), 2)}:00`, payType: 3, status: 1, remark: '地下车位服务费',
        });
      }
    }
  }
}

// ---------- 预收款 ----------
const prepays = [];
for (let i = 1; i <= 30; i++) {
  const amount = pick([1000, 2000, 3000]);
  const used = i <= 10 ? money(amount * pick([0.3, 0.5, 0.6])) : 0;
  prepays.push({
    id: i, ownerId: i, amount, used, balance: money(amount - used),
    payTime: `2026-0${rint(5, 8)}-${pad(rint(1, 28), 2)} 11:00:00`,
    payType: 3, no: `YSK-2026-${pad(i, 4)}`, status: 1, remark: '预存物业费',
  });
}
const prepayUsages = prepays.filter((p) => p.used > 0).slice(0, 8).map((p, i) => ({
  id: i + 1, prepaymentId: p.id,
  paymentId: (payments.find((x) => x.ownerId === p.ownerId) || payments[0]).id,
  amount: p.used, useTime: '2026-08-20 10:00:00',
}));

// ---------- 抄表 ----------
const meterReadings = [];
{
  let id = 0;
  const state = new Map();
  for (const room of rooms.filter((r) => r.ownerId)) {
    for (const [type, prefix, unitMin, unitMax] of [[1, 'W', 3, 12], [2, 'E', 100, 420]]) {
      let last = type === 1 ? rint(100, 500) : rint(1000, 3000);
      for (const m of ['2026-07', '2026-08']) {
        const usage = rint(unitMin, unitMax) + (type === 2 && room.status === 3 ? 0 : 0);
        const cur = last + usage;
        meterReadings.push({
          id: ++id, roomId: room.id, meterType: type,
          meterNo: `${prefix}-${room.roomCode}`,
          month: m, last, current: cur, usage,
          user: pick(['刘志刚', '孙建军']),
          date: m === '2026-07' ? '2026-08-01' : '2026-09-01',
        });
        last = cur;
      }
      state.set(`${room.id}-${type}`, last);
    }
  }
}

// ---------- 滞纳金配置 / 票据 / 家庭成员 ----------
const lateFeeConfig = [
  { id: 1, feeItemId: null, graceDays: 5, rateType: 2, rate: 0.0005, max: 200, active: 1 },
];
const invoices = [];
for (let i = 1; i <= 60; i++) {
  const st = i <= 30 ? 1 : i <= 55 ? 2 : 3;
  invoices.push({
    id: i, no: `FP-2026-${pad(i, 6)}`, type: 2,
    userId: st === 1 ? null : 3, userName: st === 1 ? null : '王雅芳',
    status: st,
    useTime: st === 2 ? '2026-08-20 15:00:00' : null,
    voidTime: st === 3 ? '2026-08-25 16:30:00' : null,
    voidReason: st === 3 ? '开票信息有误' : null,
  });
}
const familyMembers = [];
{
  let id = 0;
  for (let i = 1; i <= 80; i++) {
    const owner = owners[i - 1];
    const spouse = { id: ++id, ownerId: i, name: personName(), relation: owner.gender === 1 ? '妻' : '夫', gender: owner.gender === 1 ? 2 : 1 };
    spouse.phone = `1350000${pad(3000 + id, 4)}`;
    spouse.phoneEnc = aes(spouse.phone);
    spouse.phoneMask = `135****${spouse.phone.slice(-4)}`;
    familyMembers.push(spouse);
    if (i % 2 === 0) {
      const child = { id: ++id, ownerId: i, name: personName(), relation: owner.gender === 1 ? '子' : '女', gender: rnd() < 0.5 ? 1 : 2 };
      child.phone = null; child.phoneEnc = null; child.phoneMask = null;
      familyMembers.push(child);
    }
  }
}

// ---------- 运营数据 ----------
const opOrders = [];
const orderTitles = [
  [1, '卫生间顶板渗水报修', '主卫顶板阴湿渗水，怀疑楼上防水层破损，请师傅上门查看。'],
  [1, '厨房下水道堵塞', '厨房下水返水缓慢，疑似主管道堵塞，请尽快疏通。'],
  [1, '入户门锁打不开', '智能门锁没电且机械钥匙拧不动，需要开锁协助。'],
  [1, '客厅窗户漏水', '台风天窗户边框渗水，密封胶疑似老化。'],
  [1, '空调外机支架锈蚀', '外机支架锈蚀严重，存在高空坠物隐患，请安排检查。'],
  [2, '楼上装修噪音扰民', '楼上工作日 8 点前开始电钻作业，请按规定时间施工。'],
  [2, '地库积水投诉', '暴雨后地库入口积水严重，排水泵未及时启动。'],
  [2, '快递柜太少', '丰巢柜常年不足，快递堆放大堂无人管理。'],
  [3, '建议增设电动车充电桩', '地库电动车充电口不够，建议增加充电桩。'],
  [3, '建议儿童区增加遮阳棚', '夏季儿童游乐区暴晒，建议加装遮阳棚。'],
  [4, '门禁卡办理咨询', '如何为家里老人增办门禁卡，需要什么材料？'],
  [4, '物业费发票咨询', '线上缴费后如何领取纸质发票？'],
];
let orderSeq = 0;
for (let i = 1; i <= 40; i++) {
  const [type, title, content] = orderTitles[i % orderTitles.length];
  const room = rooms.find((r) => r.ownerId && r.id % 40 === i % 40) || rooms.find((r) => r.ownerId);
  const owner = owners[room.ownerId - 1];
  const status = i <= 4 ? 1 : i <= 14 ? 2 : i <= 20 ? 3 : i <= 36 ? 4 : 5;
  const created = `2026-08-${pad(rint(1, 28), 2)} ${pad(rint(8, 20), 2)}:${pad(rint(0, 59), 2)}:00`;
  const assigned = status >= 2;
  const handled = status >= 3;
  const visited = status >= 4;
  const handler = type === 1 ? pick([staff(7), staff(8)]) : staff(5);
  opOrders.push({
    id: i,
    no: `GD-202609-${pad(i, 4)}`,
    type, title, content,
    roomId: room.id, ownerId: room.ownerId, ownerName: owner.name, ownerPhone: owner.phoneMask,
    priority: type === 1 && i % 5 === 0 ? 1 : i % 7 === 0 ? 3 : 2,
    status,
    assignUserId: assigned ? handler.id : null,
    assignUserName: assigned ? handler.name : null,
    assignTime: assigned ? created : null,
    handleContent: handled ? '已上门处理完毕，现场清理干净，业主确认无误。' : null,
    handleTime: handled ? created : null,
    visitContent: visited ? '电话回访业主，对处理结果表示满意。' : status === 5 ? '业主自行解决，申请关闭工单。' : null,
    visitScore: visited ? rint(3, 5) : null,
    visitTime: visited ? created : null,
    closeTime: status === 5 ? created : null,
    createBy: owner.name, createTime: created,
  });
}
const orderFlows = [];
{
  let id = 0;
  for (const o of opOrders) {
    orderFlows.push({ id: ++id, orderId: o.id, flowType: 1, content: '业主提交工单', operatorId: null, operatorName: o.ownerName, time: o.createTime });
    if (o.assignTime) orderFlows.push({ id: ++id, orderId: o.id, flowType: 2, content: `分配给 ${o.assignUserName}`, operatorId: 5, operatorName: '李 婷', time: o.assignTime });
    if (o.handleTime) orderFlows.push({ id: ++id, orderId: o.id, flowType: 3, content: o.handleContent, operatorId: o.assignUserId, operatorName: o.assignUserName, time: o.handleTime });
    if (o.visitTime && o.status === 4) orderFlows.push({ id: ++id, orderId: o.id, flowType: 4, content: o.visitContent, operatorId: 6, operatorName: '周 倩', time: o.visitTime });
    if (o.status === 5) orderFlows.push({ id: ++id, orderId: o.id, flowType: 5, content: '工单关闭', operatorId: 5, operatorName: '李 婷', time: o.closeTime });
  }
}
const cleanAreas = ['1 号楼大堂', '2 号楼大堂', '中央花园', '地下车库', '儿童游乐区'];
const cleanArranges = [];
{
  let id = 0;
  for (let day = 25; day <= 31; day++) {
    for (const [ai, area] of cleanAreas.entries()) {
      const cleaner = CLEANERS[(day + ai) % CLEANERS.length];
      const date = `2026-08-${pad(day, 2)}`;
      const status = day < 31 ? 3 : 1;
      cleanArranges.push({
        id: ++id, area, cleanType: ai === 3 ? 2 : 1, arrangeDate: date,
        startTime: '07:30', endTime: '11:30',
        cleanerId: cleaner.id, cleanerName: cleaner.name,
        status, completeTime: status === 3 ? `${date} 11:20:00` : null,
        remark: null,
      });
    }
  }
}
const cleanChecks = cleanArranges.filter((_, i) => i % 5 === 0).map((a, i) => ({
  id: i + 1, arrangeId: a.id, checkDate: a.arrangeDate, areaName: a.area,
  checkResult: i === 3 ? 2 : 1, score: i === 3 ? 62 : rint(85, 98),
  problems: i === 3 ? '地库排水沟有落叶堆积，未及时清理' : null,
  checkerId: 9, checkerName: '赵铁军',
}));
const fireFacilities = [];
{
  let id = 0;
  for (let b = 1; b <= 6; b++) {
    for (let n = 1; n <= 4; n++) fireFacilities.push({ id: ++id, b, name: '干粉灭火器', type: 1, no: `MHQ-B${pad(b, 2)}-${pad(n, 2)}`, location: `${b} 号楼 ${n <= 2 ? n * 5 : '地库'} 层消防柜`, install: '2023-06-30', expire: n % 6 === 0 ? '2026-08-31' : '2026-12-31' });
    for (let n = 1; n <= 2; n++) fireFacilities.push({ id: ++id, b, name: '室内消火栓', type: 2, no: `XHS-B${pad(b, 2)}-${pad(n, 2)}`, location: `${b} 号楼消火栓井`, install: '2023-06-30', expire: '2027-06-30' });
    for (let n = 1; n <= 4; n++) fireFacilities.push({ id: ++id, b, name: '应急照明灯', type: 5, no: `YJD-B${pad(b, 2)}-${pad(n, 2)}`, location: `${b} 号楼疏散通道`, install: '2023-06-30', expire: '2027-06-30' });
  }
  fireFacilities.forEach((f, i) => {
    f.status = f.expire === '2026-08-31' ? 4 : i === 10 ? 2 : i === 11 ? 3 : 1;
    f.lastCheck = '2026-08-28'; f.nextCheck = '2026-09-28';
  });
}
const firePatrols = [];
for (let day = 28; day <= 31; day++) {
  const bad = day === 30;
  firePatrols.push({
    id: day - 27, date: `2026-08-${pad(day, 2)}`, time: '09:00:00',
    area: `${pick([1, 2, 3])} 号楼楼道及地库`,
    result: bad ? 2 : 1,
    problems: bad ? '3 号楼地库一处应急灯不亮' : null,
    userId: 9011, userName: '赵建国',
    handle: bad ? '已报工程部更换应急灯，当日完成' : null,
    status: bad ? 2 : 1,
  });
}
[29, 30, 31, 1, 2, 3, 4, 5].forEach((day, i) => {
  const m = day > 20 ? '2026-08' : '2026-09';
  const bad = i === 4;
  firePatrols.push({
    id: firePatrols.length + 1, date: `${m}-${pad(day, 2)}`, time: '09:00:00',
    area: `${pick([4, 5, 6])} 号楼楼道及地库`,
    result: bad ? 2 : 1,
    problems: bad ? '5 号楼地库消防通道堆放装修材料' : null,
    userId: 9012, userName: '钱 进',
    handle: bad ? '已通知业主当天清运完毕' : null,
    status: bad ? 2 : 1,
  });
});
const fireDrills = [
  { id: 1, name: '夏季消防疏散演练', type: 2, date: '2026-06-15', start: '09:00:00', end: '11:00:00', location: '中心广场及 1-6 号楼', count: 86, content: '模拟 3 号楼 5 层电气火灾，组织业主疏散逃生、初期扑救及伤员救护演练。', summary: '全员疏散用时 6 分 30 秒，达到预案要求；个别业主不熟悉疏散路线，已安排楼栋宣传。', organizer: '张伟民', status: 3 },
  { id: 2, name: '灭火器实操培训演练', type: 1, date: '2026-09-20', start: '09:30:00', end: '11:00:00', location: '小区东门广场', count: null, content: '组织秩序队员、保洁及业主代表进行干粉灭火器实操培训。', summary: null, organizer: '赵铁军', status: 1 },
];
const securityArranges = [];
const dutyRecords = [];
{
  let id = 0, did = 0;
  const positions = ['东门岗', '监控中心', '西门岗'];
  for (let day = 1; day <= 6; day++) {
    const date = `2026-09-${pad(day, 2)}`;
    for (const [si, shift] of [[1, '早班'], [2, '中班'], [3, '晚班']].entries()) {
      for (const pos of positions.slice(0, 2)) {
        const g = GUARDS[(day + si + positions.indexOf(pos)) % GUARDS.length];
        securityArranges.push({
          id: ++id, date, shiftType: shift === '早班' ? 1 : shift === '中班' ? 2 : 3,
          start: shift === '早班' ? '07:00:00' : shift === '中班' ? '15:00:00' : '23:00:00',
          end: shift === '早班' ? '15:00:00' : shift === '中班' ? '23:00:00' : '07:00:00',
          position: pos, securityId: g.id, securityName: g.name,
          status: day <= 5 ? 3 : 1, remark: null,
        });
      }
    }
    dutyRecords.push({
      id: ++did, date, start: '23:00:00', end: '07:00:00', position: '监控中心',
      securityId: GUARDS[did % GUARDS.length].id, securityName: GUARDS[did % GUARDS.length].name,
      content: '夜间定时巡逻打点，监控轮巡正常，无异常报警。',
      abnormal: did === 2 ? '凌晨 2:40 东门外卖摊贩聚集，已劝离' : null,
      status: did === 2 ? 2 : 1,
    });
  }
}
const visitRecords = [];
for (let i = 1; i <= 25; i++) {
  const room = rooms.find((r) => r.ownerId && (r.id % 53) === i % 53) || rooms[0];
  const leave = i <= 20;
  visitRecords.push({
    id: i,
    name: personName(),
    phone: `150${pad(rint(10000000, 99999999), 8)}`,
    reason: pick(['上门探访业主', '装修材料送货', '家政服务', '房产中介带看', '外卖配送']),
    target: owners[room.ownerId - 1].name,
    roomId: room.id,
    visitTime: `2026-09-0${rint(4, 5)} ${pad(rint(9, 18), 2)}:${pad(rint(0, 59), 2)}:00`,
    leaveTime: leave ? `2026-09-0${rint(4, 5)} ${pad(rint(19, 21), 2)}:00:00` : null,
    count: rint(1, 3),
    plate: rnd() < 0.5 ? `浙A${pick('ABCDEFGHJKLMNPQ')}${pad(rint(10000, 99999), 5)}` : null,
    guardId: 9011, guardName: '赵建国',
    status: leave ? 2 : 1,
  });
}
const vehicleRecords = [];
{
  let id = 0;
  const monthlyPlates = Array.from({ length: 20 }, (_, i) => `浙A${pick('BCDFGH')}${pad(rint(10000, 99999), 5)}`);
  for (const day of ['2026-09-04', '2026-09-05']) {
    for (const [i, plate] of monthlyPlates.entries()) {
      const inTime = `${day} ${pad(rint(7, 9), 2)}:${pad(rint(0, 59), 2)}:00`;
      vehicleRecords.push({ id: ++id, plate, vehicleType: 1, recordType: 1, recordTime: inTime, gate: '东门', parkingId: i + 41, isTemp: 0, fee: null, payStatus: null, payTime: null });
      if (rnd() < 0.7) vehicleRecords.push({ id: ++id, plate, vehicleType: 1, recordType: 2, recordTime: `${day} ${pad(rint(17, 21), 2)}:${pad(rint(0, 59), 2)}:00`, gate: '东门', parkingId: i + 41, isTemp: 0, fee: null, payStatus: null, payTime: null });
    }
    for (let t = 0; t < 8; t++) {
      const plate = `浙A${pick('EFGHJK')}${pad(rint(30000, 99999), 5)}`;
      const inTime = `${day} ${pad(rint(10, 16), 2)}:${pad(rint(0, 59), 2)}:00`;
      const fee = pick([5, 10, 15, 20]);
      vehicleRecords.push({ id: ++id, plate, vehicleType: 1, recordType: 1, recordTime: inTime, gate: '西门', parkingId: null, isTemp: 1, fee: null, payStatus: null, payTime: null });
      vehicleRecords.push({ id: ++id, plate, vehicleType: 1, recordType: 2, recordTime: `${day} ${pad(rint(17, 20), 2)}:${pad(rint(0, 59), 2)}:00`, gate: '西门', parkingId: null, isTemp: 1, fee, payStatus: 1, payTime: `${day} ${pad(rint(17, 20), 2)}:${pad(rint(0, 59), 2)}:00` });
    }
  }
}
const goodsRecords = Array.from({ length: 10 }, (_, i) => {
  const room = rooms.find((r) => r.ownerId && r.id % 37 === i % 37) || rooms[0];
  const isIn = i % 2 === 0;
  return {
    id: i + 1, recordType: isIn ? 1 : 2,
    goods: pick(['瓷砖 20 箱', '水泥 10 袋', '沙发 3 件套', '实木床 1 张', '空调外机 1 台', '衣柜板材 12 件']),
    desc: isIn ? '装修材料搬运入小区' : '旧家具搬出处理',
    quantity: rint(1, 20),
    ownerName: owners[room.ownerId - 1].name, roomId: room.id,
    operator: personName(), operatorPhone: `1580000${pad(4000 + i, 4)}`,
    time: `2026-09-0${rint(1, 5)} ${pad(rint(8, 18), 2)}:00:00`,
    guardId: 9013, guardName: '孙 立',
  };
});
const greenery = [
  { name: '香樟', type: 1, location: '中央大道两侧', quantity: 26, plant: '2023-05-10' },
  { name: '银杏', type: 1, location: '南门入口两侧', quantity: 12, plant: '2023-05-10' },
  { name: '紫薇', type: 1, location: '中心广场周边', quantity: 18, plant: '2023-05-12' },
  { name: '红叶石楠', type: 2, location: '各楼间绿化带', quantity: 160, plant: '2023-05-15' },
  { name: '金森女贞', type: 2, location: '地下车库出入口', quantity: 80, plant: '2023-05-15' },
  { name: '桂花', type: 2, location: '中心广场', quantity: 22, plant: '2023-05-15' },
  { name: '狗牙根草坪', type: 3, location: '中央花园', quantity: 1, plant: '2023-05-20' },
  { name: '马尼拉草坪', type: 3, location: '儿童游乐区周边', quantity: 1, plant: '2023-05-20' },
  { name: '月季', type: 4, location: '南门花坛', quantity: 220, plant: '2023-05-22' },
  { name: '绣球', type: 4, location: '1 号楼前花坛', quantity: 90, plant: '2023-05-22' },
  { name: '樱花', type: 1, location: '儿童游乐区', quantity: 8, plant: '2024-03-15' },
  { name: '垂丝海棠', type: 1, location: '2 号楼前', quantity: 10, plant: '2024-03-15' },
  { name: '茶梅', type: 2, location: '西门外围', quantity: 60, plant: '2024-03-18' },
  { name: '鸢尾', type: 4, location: '水景周边', quantity: 150, plant: '2024-04-02' },
  { name: '冬青篱', type: 2, location: '小区围墙内侧', quantity: 300, plant: '2024-04-02' },
].map((g, i) => ({ id: i + 1, ...g, status: i === 11 ? 2 : 1, remark: i === 11 ? '长势不佳已安排养护' : null }));
const greeneryChecks = Array.from({ length: 8 }, (_, i) => ({
  id: i + 1, date: `2026-08-${pad(3 + i * 3, 2)}`,
  area: pick(['中央花园', '中央大道', '南门花坛', '楼间绿化带']),
  result: i === 2 ? 2 : 1, score: i === 2 ? 70 : rint(85, 97),
  problems: i === 2 ? '垂丝海棠虫害（蚜虫），长势不佳' : null,
  checkerId: GREEN_WORKER.id, checkerName: GREEN_WORKER.name,
}));
const activities = [
  { id: 1, name: '端午邻里包粽子活动', type: 3, date: '2026-06-19', start: '14:00:00', end: '17:00:00', location: '中心广场', content: '组织业主包粽子、做香囊，物业提供材料并评选"巧手之家"。', count: 120, budget: 3000, cost: 2860, organizer: '李 婷', status: 3 },
  { id: 2, name: '暑期少儿绘画比赛', type: 2, date: '2026-07-20', start: '09:00:00', end: '11:30:00', location: '社区活动室', content: '以"我的美丽家园"为主题，6-12 岁少儿绘画比赛，评出一二三等奖。', count: 45, budget: 1500, cost: 1320, organizer: '周 倩', status: 3 },
  { id: 3, name: '中秋游园会', type: 3, date: '2026-09-24', start: '18:30:00', end: '21:00:00', location: '中心广场', content: '中秋灯谜会、游园闯关与月饼品鉴，物资正在采购中。', count: null, budget: 5000, cost: null, organizer: '李 婷', status: 1 },
  { id: 4, name: '重阳节爱心义诊', type: 1, date: '2026-10-11', start: '09:00:00', end: '11:30:00', location: '社区活动室', content: '联合社区卫生服务中心为老年业主提供血压血糖检测与问诊。', count: null, budget: 800, cost: null, organizer: '张伟民', status: 1 },
];
const notices = [
  { id: 1, title: '关于台风"海鸥"的紧急温馨提示', type: 4, top: 1, publish: '2026-08-30 09:00:00', expire: '2026-09-05 23:59:59', read: 176, status: 2, content: '预计 9 月 1 日前后台风影响我市，请业主关好门窗、收起阳台悬挂物，检查地漏；地下车库出入口将视情关闭，请关注群内通知。' },
  { id: 2, title: '2026 年 8 月物业费缴费提醒', type: 1, top: 0, publish: '2026-08-01 10:00:00', expire: '2026-08-31 23:59:59', read: 154, status: 2, content: '8 月物业管理费已出账，缴费截止日为 8 月 25 日，可通过线上缴费或至物业服务中心缴纳，逾期将按约定收取滞纳金。' },
  { id: 3, title: '中秋游园会活动预告', type: 2, top: 1, publish: '2026-09-01 14:00:00', expire: '2026-09-24 23:59:59', read: 98, status: 2, content: '9 月 24 日晚中心广场中秋游园会，灯谜会、月饼品鉴、游园闯关，欢迎业主报名参加。' },
  { id: 4, title: '电梯年度维保作业通知', type: 1, top: 0, publish: '2026-09-02 09:00:00', expire: '2026-09-10 23:59:59', read: 76, status: 2, content: '9 月 5 日至 9 月 6 日对 1-6 号楼电梯进行年度维保，作业时段电梯轮换停运约 2 小时，请合理安排出行。' },
  { id: 5, title: '高温天气用电安全提示', type: 3, top: 0, publish: '2026-08-12 10:00:00', expire: '2026-08-31 23:59:59', read: 132, status: 2, content: '近期持续高温，请勿超负荷用电，离家请拔插头；电动车请勿入户充电。' },
  { id: 6, title: '小区高空抛物监控启用公告', type: 2, top: 0, publish: '2026-07-15 10:00:00', expire: '2026-12-31 23:59:59', read: 143, status: 2, content: '为保障"头顶上的安全"，小区已在高空抛物多发点位安装朝上监控，仅拍摄楼体区域，不涉及住户室内与阳台。' },
  { id: 7, title: '9 月 6 日临时停水通知', type: 4, top: 0, publish: '2026-09-04 16:00:00', expire: '2026-09-06 23:59:59', read: 89, status: 2, content: '市政管网改造，9 月 6 日 09:00-12:00 小区临时停水，请提前储水。给您带来不便敬请谅解。' },
  { id: 8, title: '业主大会筹备公告（草稿）', type: 2, top: 0, publish: null, expire: null, read: 0, status: 1, content: '业委会换届筹备中，具体时间另行通知。' },
];
const messages = [
  { id: 1, title: '工单提醒：卫生间渗水报修待分配', receiver: 5, receiverName: '李 婷', type: 1, content: '业主提交报修工单 GD-202609-0001，请及时分配处理。', businessType: 'service_order', businessId: 1 },
  { id: 2, title: '工单提醒：厨房下水道堵塞待分配', receiver: 5, receiverName: '李 婷', type: 1, content: '业主提交报修工单 GD-202609-0002，请及时分配处理。', businessType: 'service_order', businessId: 2 },
  { id: 3, title: '维保工单已派单', receiver: 7, receiverName: '刘志刚', type: 1, content: '您有新的维修工单，请及时上门处理。', businessType: 'service_order', businessId: 3 },
  { id: 4, title: '缴费对账提醒', receiver: 3, receiverName: '王雅芳', type: 1, content: '2026 年 8 月收费对账单已生成，请及时复核。', businessType: 'finance', businessId: null },
  { id: 5, title: '消防安全检查提醒', receiver: 9, receiverName: '赵铁军', type: 1, content: '本月消防设施巡检已完成 80%，请按计划推进。', businessType: 'fire', businessId: null },
  { id: 6, title: '短信通知示例（缴费提醒）', receiver: 6, receiverName: '周 倩', type: 2, content: '【云栖湾物业】您 8 月物业费已出账，请及时缴纳。', businessType: 'sms', businessId: null },
];
const opinionBoxes = [
  { id: 1, name: '1 号楼大厅意见箱', admin: 5, adminName: '李 婷', anonymous: 1, active: 1, remark: '每周一、四开箱' },
  { id: 2, name: '线上意见箱', admin: 5, adminName: '李 婷', anonymous: 1, active: 1, remark: '小程序/前台同步受理' },
];
const opinions = [
  { id: 1, box: 2, title: '早晚高峰电梯等待时间过长', content: '早高峰 2 单元电梯经常一层停满直上高层，建议错峰调度。', anonymous: 0, user: '钱国栋', status: 3, reply: '已联系维保单位调整高峰期运行策略，并加贴错峰提示。', replyUser: '李 婷', satisfaction: 4 },
  { id: 2, box: 2, title: '绿化带宠物粪便清理不及时', content: '中央花园草坪宠物粪便较多，希望加强保洁巡查。', anonymous: 1, user: null, status: 3, reply: '已在草坪增设宠物便纸箱与提示牌，保洁每日两巡。', replyUser: '李 婷', satisfaction: 5 },
  { id: 3, box: 2, title: '快递柜数量不足', content: '快递高峰期柜子不够，希望协调增加一组快递柜。', anonymous: 0, user: '孙晓梅', status: 2, reply: null, replyUser: null, satisfaction: null },
  { id: 4, box: 1, title: '地下车库照明偏暗', content: 'B1 区照明灯有损坏，行车安全隐患。', anonymous: 0, user: '吴建国', status: 3, reply: '损坏灯具已全部更换为 LED 灯，并加装反光标识。', replyUser: '李 婷', satisfaction: 4 },
  { id: 5, box: 2, title: '希望增设健身器材', content: '小区缺少适老化健身器材，建议在中心花园一角增设。', anonymous: 0, user: '郑海生', status: 1, reply: null, replyUser: null, satisfaction: null },
  { id: 6, box: 2, title: '装修运输车辆占用消防通道', content: '装修高峰期板车停在消防通道上，请加强管理。', anonymous: 1, user: null, status: 2, reply: null, replyUser: null, satisfaction: null },
  { id: 7, box: 1, title: '楼道堆物问题', content: '个别楼层楼道堆放纸箱杂物，存在消防隐患。', anonymous: 0, user: '冯丽华', status: 3, reply: '已联合业委会上门劝导清理，并每周巡查通报。', replyUser: '李 婷', satisfaction: 5 },
  { id: 8, box: 2, title: '建议增设楼栋雨棚', content: '单元门口雨天积水，建议加装雨棚。', anonymous: 0, user: '许志强', status: 4, reply: '经业委会商议暂不安装，已做好门口排水改造。', replyUser: '李 婷', satisfaction: 3 },
];
const surveys = [
  {
    id: 1, title: '小区电动自行车充电桩增设点位投票', desc: '为解决电动车充电难，拟增设充电桩，请业主对点位方案投票。', type: 1,
    start: '2026-09-01 09:00:00', end: '2026-09-10 23:59:59', anonymous: 0, multiple: 0, participants: 95, status: 2,
    options: [
      { id: 1, content: '方案一：地下车库 B1 区加装 40 个充电位', votes: 45 },
      { id: 2, content: '方案二：5 号楼西侧地面加装 20 个充电位', votes: 38 },
      { id: 3, content: '暂不增设', votes: 12 },
    ],
  },
  {
    id: 2, title: '2026 年上半年物业服务满意度问卷', desc: '请业主对上半年物业服务整体水平进行评价。', type: 2,
    start: '2026-08-01 09:00:00', end: '2026-08-20 23:59:59', anonymous: 1, multiple: 0, participants: 164, status: 3,
    options: [
      { id: 4, content: '满意', votes: 120 },
      { id: 5, content: '一般', votes: 35 },
      { id: 6, content: '不满意', votes: 9 },
    ],
  },
];
const surveyVotes = [];
{
  let id = 0;
  for (const s of surveys) {
    s.options.forEach((o) => {
      for (let i = 0; i < o.votes; i++) {
        surveyVotes.push({ id: ++id, surveyId: s.id, optionId: o.id, userId: null, voteTime: `${s.start.slice(0, 10)} ${pad(rint(9, 21), 2)}:${pad(rint(0, 59), 2)}:00` });
      }
    });
  }
}
const committeeMembers = [
  { id: 1, name: '钱国栋', position: '业主委员会主任', roomId: 3 },
  { id: 2, name: '吴秀兰', position: '业主委员会副主任', roomId: 8 },
  { id: 3, name: '郑海生', position: '委员（工程监督）', roomId: 15 },
  { id: 4, name: '冯丽华', position: '委员（环境卫生）', roomId: 42 },
  { id: 5, name: '许志强', position: '委员（安全秩序）', roomId: 77 },
  { id: 6, name: '孙晓梅', position: '委员（文体活动）', roomId: 120 },
  { id: 7, name: '何俊', position: '委员（财务监督）', roomId: 180 },
].map((m, i) => ({ ...m, phone: `1370000${pad(6000 + i, 4)}`, termStart: '2025-01-01', termEnd: '2028-12-31', status: 1, introduction: '热心小区公共事务，业主推选产生。' }));
const committeeMeetings = [
  {
    id: 1, title: '业委会 2026 年第一季度工作会议', date: '2026-03-15', start: '19:00:00', end: '21:00:00', location: '社区活动室',
    content: '审议 2025 年度物业收支公示报告；讨论电梯维保续约方案；通报上年度业主意见处理情况。',
    summary: '一致通过 2025 年度收支公示；电梯维保合同续签一年并新增高峰调度条款；意见处理满意率 92%。',
    attendees: '业委会全体成员、物业经理张伟民、客服主管李婷', status: 3,
  },
  {
    id: 2, title: '物业服务续聘评议会', date: '2026-09-12', start: '19:00:00', end: '21:00:00', location: '社区活动室',
    content: '对物业服务合同续聘进行评议，审议下半年品质提升计划（含充电桩增设、地库照明改造）。',
    summary: null, attendees: '业委会全体成员、物业项目经理', status: 1,
  },
];
const regulations = [
  { id: 1, title: '云栖湾小区装修管理规约', category: '装修管理', content: '装修时间：工作日 8:30-12:00、14:00-18:00；禁止拆改承重结构；装修垃圾装袋后堆放至指定点位，当日清运；装修押金验收合格后 15 个工作日内退还。' },
  { id: 2, title: '小区机动车停放管理办法', category: '停车管理', content: '地下车位产权/租赁用户按月缴纳车位服务费；临时车辆按 5 元/小时、24 小时封顶 20 元收取；消防通道禁止停车，违者锁车并公示。' },
  { id: 3, title: '小区环境卫生管理公约', category: '环境卫生', content: '生活垃圾定点投放、定时清运；装修垃圾与大件垃圾预约投放；饲养宠物须束绳并即时清理排泄物。' },
  { id: 4, title: '门禁卡与人脸识别使用规定', category: '出入管理', content: '每户免费办理门禁卡 3 张，增办 20 元/张；人脸信息仅用于门禁核验，业主可申请关闭；卡片遗失请及时挂失补办。' },
  { id: 5, title: '物业费收缴管理办法', category: '收费管理', content: '物业费按季度预缴，缴费截止日为每季度首月 25 日；逾期按日万分之五加收滞纳金；对困难家庭可申请减免，由业委会审议。' },
].map((r) => ({ ...r, isPublish: 1, publishTime: '2024-01-01 09:00:00', viewCount: rint(120, 400), status: 2 }));

// =====================================================================
// SQL 输出
// =====================================================================
function insert(table, cols, rows, opts = {}) {
  const head = `INSERT INTO ${table} (${cols.join(', ')}) VALUES\n`;
  const body = rows.map((r) => `(${r.map((v) => (v === null || v === undefined ? 'NULL' : v)).join(', ')})`).join(',\n');
  return head + body + ';\n\n';
}
const del = (table, where) => `DELETE FROM ${table} WHERE ${where};\n`;

// ---- system ----
let sys = `-- ============================================================
-- SmartProperty 演示种子数据 - 系统基础库（由 scripts/seed/generate-seed.mjs 生成）
-- 场景：和家云物业 · 云栖湾小区（业务团队/账号/字典），幂等可重复执行
-- 业务用户密码统一为 Aa123456（BCrypt）
-- ============================================================
USE \`smart_property_system\`;\nSET NAMES utf8mb4;\n\n`;
sys += del('sys_user', 'id BETWEEN 2 AND 99') + del('sys_user_role', 'user_id > 1') + del('sys_role', 'id > 1') + del('sys_dept', 'id > 3') + del('sys_dict_type', 'id <= 99') + del('sys_dict_data', 'id <= 999') + '\n';
sys += insert('sys_dept', ['id', 'company_id', 'parent_id', 'dept_name', 'sort', 'status', 'create_by'],
  [[4, 1, 1, q('客服部'), 3, 1, q('system')], [5, 1, 1, q('财务部'), 4, 1, q('system')], [6, 1, 1, q('工程部'), 5, 1, q('system')], [7, 1, 1, q('秩序维护部'), 6, 1, q('system')]]);
sys += insert('sys_role', ['id', 'company_id', 'role_name', 'role_key', 'sort', 'status', 'create_by'],
  [[2, 1, q('物业经理'), q('property_manager'), 1, 1, q('system')],
   [3, 1, q('财务专员'), q('finance_officer'), 2, 1, q('system')],
   [4, 1, q('客服专员'), q('customer_service'), 3, 1, q('system')],
   [5, 1, q('维修工程师'), q('maintenance_engineer'), 4, 1, q('system')],
   [6, 1, q('秩序班长'), q('security_squad'), 5, 1, q('system')]]);
sys += insert('sys_user', ['id', 'company_id', 'dept_id', 'username', 'password', 'real_name', 'phone', 'phone_mask', 'gender', 'status', 'remark', 'create_by'],
  STAFF.map((s) => [s.id, 1, s.dept, q(s.username), q(BCRYPT_HASH), q(s.name), q(aes(s.phone)), q(`${s.phone.slice(0, 3)}****${s.phone.slice(-4)}`), s.gender, 1, q(s.title), q('system')]));
sys += insert('sys_user_role', ['user_id', 'role_id'], STAFF.map((s) => [s.id, s.role]));
sys += insert('sys_dict_type', ['id', 'dict_name', 'dict_type', 'status', 'remark', 'create_by'],
  [[1, q('工单类型'), q('order_type'), 1, q('服务工单类型'), q('system')],
   [2, q('工单优先级'), q('order_priority'), 1, null, q('system')],
   [3, q('支付方式'), q('pay_type'), 1, null, q('system')],
   [4, q('收费模式'), q('charge_mode'), 1, null, q('system')],
   [5, q('仪表类型'), q('meter_type'), 1, null, q('system')],
   [6, q('系统开关状态'), q('sys_normal_disable'), 1, null, q('system')],
   [7, q('用户性别'), q('sys_user_sex'), 1, null, q('system')],
   [8, q('房间状态'), q('room_status'), 1, null, q('system')],
   [9, q('房间类型'), q('room_type'), 1, null, q('system')],
   [10, q('装修标准'), q('room_decoration'), 1, null, q('system')],
   [11, q('楼宇类型'), q('building_type'), 1, null, q('system')],
   [12, q('业主类型'), q('owner_type'), 1, null, q('system')],
   [13, q('装修进度状态'), q('decoration_status'), 1, null, q('system')],
   [14, q('押金状态'), q('deposit_status'), 1, null, q('system')],
   [15, q('验房状态'), q('check_status'), 1, null, q('system')],
   [16, q('验房类型'), q('check_type'), 1, null, q('system')],
   [17, q('验房结果'), q('check_result'), 1, null, q('system')],
   [18, q('租赁合同状态'), q('lease_status'), 1, null, q('system')],
   [19, q('租赁类型'), q('lease_type'), 1, null, q('system')],
   [20, q('缴费周期'), q('pay_cycle'), 1, null, q('system')],
   [21, q('费项类型'), q('fee_type'), 1, null, q('system')],
   [22, q('账单周期'), q('billing_cycle'), 1, null, q('system')],
   [23, q('账单状态'), q('ledger_status'), 1, null, q('system')],
   [24, q('缴费记录状态'), q('payment_status'), 1, null, q('system')],
   [25, q('预收款状态'), q('prepayment_status'), 1, null, q('system')],
   [26, q('车位类型'), q('parking_type'), 1, null, q('system')],
   [27, q('车位状态'), q('parking_status'), 1, null, q('system')],
   [28, q('票据类型'), q('invoice_type'), 1, null, q('system')],
   [29, q('票据状态'), q('invoice_status'), 1, null, q('system')],
   [30, q('操作类型'), q('oper_type'), 1, null, q('system')],
   [31, q('登录/操作结果'), q('sys_common_status'), 1, null, q('system')]]);
const dictData = [
  [1, 'order_type', '报修', 1, 1], [2, 'order_type', '投诉', 2, 2], [3, 'order_type', '建议', 3, 3], [4, 'order_type', '咨询', 4, 4],
  [5, 'order_priority', '紧急', 1, 1], [6, 'order_priority', '普通', 2, 2], [7, 'order_priority', '低', 3, 3],
  [8, 'pay_type', '现金', 1, 1], [9, 'pay_type', '转账', 2, 2], [10, 'pay_type', '微信', 3, 3], [11, 'pay_type', '支付宝', 4, 4], [12, 'pay_type', '预收款', 5, 5],
  [13, 'charge_mode', '按面积', 1, 1], [14, 'charge_mode', '按户', 2, 2], [15, 'charge_mode', '按用量', 3, 3],
  [16, 'meter_type', '水表', 1, 1], [17, 'meter_type', '电表', 2, 2], [18, 'meter_type', '气表', 3, 3],
  [19, 'sys_normal_disable', '停用', 0, 1], [20, 'sys_normal_disable', '正常', 1, 2],
  [21, 'sys_user_sex', '未知', 0, 1], [22, 'sys_user_sex', '男', 1, 2], [23, 'sys_user_sex', '女', 2, 3],
  [24, 'room_status', '空置', 1, 1], [25, 'room_status', '已售', 2, 2], [26, 'room_status', '已租', 3, 3], [27, 'room_status', '装修中', 4, 4],
  [28, 'room_type', '住宅', 1, 1], [29, 'room_type', '商业', 2, 2], [30, 'room_type', '办公', 3, 3], [31, 'room_type', '仓库', 4, 4], [32, 'room_type', '车库', 5, 5],
  [33, 'room_decoration', '毛坯', 1, 1], [34, 'room_decoration', '简装', 2, 2], [35, 'room_decoration', '精装', 3, 3],
  [36, 'building_type', '住宅', 1, 1], [37, 'building_type', '商业', 2, 2], [38, 'building_type', '办公', 3, 3], [39, 'building_type', '仓库', 4, 4], [40, 'building_type', '车库', 5, 5],
  [41, 'owner_type', '个人', 1, 1], [42, 'owner_type', '企业', 2, 2],
  [43, 'decoration_status', '申请中', 1, 1], [44, 'decoration_status', '施工中', 2, 2], [45, 'decoration_status', '已完工', 3, 3], [46, 'decoration_status', '已验收', 4, 4],
  [47, 'deposit_status', '未缴', 1, 1], [48, 'deposit_status', '已缴', 2, 2], [49, 'deposit_status', '已退', 3, 3],
  [50, 'check_status', '待处理', 1, 1], [51, 'check_status', '已整改', 2, 2],
  [52, 'check_type', '物业验房', 1, 1], [53, 'check_type', '业主验房', 2, 2],
  [54, 'check_result', '合格', 1, 1], [55, 'check_result', '不合格', 2, 2],
  [56, 'lease_status', '草稿', 1, 1], [57, 'lease_status', '生效', 2, 2], [58, 'lease_status', '已终止', 3, 3], [59, 'lease_status', '已到期', 4, 4],
  [60, 'lease_type', '整租', 1, 1], [61, 'lease_type', '分租', 2, 2],
  [62, 'pay_cycle', '月付', 1, 1], [63, 'pay_cycle', '季付', 2, 2], [64, 'pay_cycle', '半年付', 3, 3], [65, 'pay_cycle', '年付', 4, 4],
  [66, 'fee_type', '常规', 1, 1], [67, 'fee_type', '公摊', 2, 2], [68, 'fee_type', '临时', 3, 3], [69, 'fee_type', '临客', 4, 4],
  [70, 'billing_cycle', '月', 1, 1], [71, 'billing_cycle', '季', 2, 2], [72, 'billing_cycle', '半年', 3, 3], [73, 'billing_cycle', '年', 4, 4],
  [74, 'ledger_status', '未收', 1, 1], [75, 'ledger_status', '部分收', 2, 2], [76, 'ledger_status', '已收', 3, 3],
  [77, 'payment_status', '正常', 1, 1], [78, 'payment_status', '已退款', 2, 2], [79, 'payment_status', '已作废', 3, 3],
  [80, 'prepayment_status', '正常', 1, 1], [81, 'prepayment_status', '已退款', 2, 2],
  [82, 'parking_type', '地上', 1, 1], [83, 'parking_type', '地下', 2, 2], [84, 'parking_type', '机械', 3, 3],
  [85, 'parking_status', '空闲', 1, 1], [86, 'parking_status', '已售', 2, 2], [87, 'parking_status', '已租', 3, 3],
  [88, 'invoice_type', '收据', 1, 1], [89, 'invoice_type', '发票', 2, 2],
  [90, 'invoice_status', '未使用', 1, 1], [91, 'invoice_status', '已使用', 2, 2], [92, 'invoice_status', '已作废', 3, 3],
  [93, 'oper_type', '其它', 0, 1], [94, 'oper_type', '新增', 1, 2], [95, 'oper_type', '修改', 2, 3], [96, 'oper_type', '删除', 3, 4], [97, 'oper_type', '查询', 4, 5], [98, 'oper_type', '导出', 5, 6],
  [99, 'sys_common_status', '失败', 0, 1], [100, 'sys_common_status', '成功', 1, 2],
];
sys += insert('sys_dict_data', ['id', 'dict_type', 'dict_label', 'dict_value', 'sort', 'status', 'create_by'],
  dictData.map(([id, t, label, v, sort]) => [id, q(t), q(label), v, sort, 1, q('system')]));

writeFileSync(join(OUT_DIR, 'smart_property_system-seed.sql'), sys);
console.log('system seed ok');

// =====================================================================
// 房产财务库
// =====================================================================
let prop = `-- ============================================================
-- SmartProperty 演示种子数据 - 房产财务库（由 scripts/seed/generate-seed.mjs 生成）
-- 场景：云栖湾小区（6 栋 11 层 2 单元 264 户 + 120 车位），幂等可重复执行
-- 敏感字段使用 EncryptUtils 同款 AES-128-ECB 加密（encrypt.key 明文密钥）
-- ============================================================
USE \`smart_property_property\`;\nSET NAMES utf8mb4;\n\n`;
const propDel = (t, where) => `DELETE FROM ${t} WHERE ${where};\n`;
prop += ['property_community|company_id=1', 'property_building|community_id=1', 'property_unit|company_id=1',
  'property_room|community_id=1', 'property_owner|company_id=1', 'property_family_member|company_id=1',
  'property_owner_room|company_id=1', 'property_sale_contract|company_id=1', 'property_tenant|company_id=1',
  'property_lease_contract|company_id=1', 'property_check_record|company_id=1', 'property_decoration_record|company_id=1',
  'finance_fee_item|company_id=1', 'finance_ladder_config|company_id=1', 'finance_ledger|company_id=1',
  'finance_payment|company_id=1', 'finance_payment_detail|company_id=1', 'finance_parking_space|company_id=1',
  'finance_parking_payment|company_id=1', 'finance_prepayment|company_id=1', 'finance_prepayment_usage|company_id=1',
  'finance_meter_reading|company_id=1', 'finance_late_fee_config|company_id=1', 'finance_invoice|company_id=1',
].map((x) => propDel(...x.split('|'))).join('') + '\n';

prop += insert('property_community', ['id', 'company_id', 'community_name', 'community_code', 'address', 'area', 'building_count', 'room_count', 'property_fee', 'contact_name', 'contact_phone', 'status', 'create_by'],
  [[1, 1, q(COMMUNITY_NAME), q(COMMUNITY_CODE), q(COMMUNITY_ADDR), '42000.00', 6, 264, '2.8000', q('张伟民'), q(aes('13900001001')), 1, q('system')]]);
prop += insert('property_building', ['id', 'company_id', 'community_id', 'building_name', 'building_code', 'building_type', 'floor_count', 'room_count', 'area', 'build_year', 'status', 'create_by'],
  buildings.map((b) => [b.id, 1, b.communityId, q(b.name), q(b.code), b.type, b.floorCount, b.roomCount, b.area, b.buildYear, 1, q('system')]));
prop += insert('property_unit', ['id', 'company_id', 'building_id', 'unit_name', 'unit_code', 'floor_count', 'room_count', 'sort', 'status', 'create_by'],
  units.map((u) => [u.id, 1, u.buildingId, q(u.name), q(u.code), u.floorCount, u.roomCount, u.sort, 1, q('system')]));
prop += insert('property_room', ['id', 'company_id', 'community_id', 'building_id', 'unit_id', 'room_code', 'room_no', 'floor', 'room_type', 'build_area', 'inner_area', 'public_area', 'orientation', 'decoration', 'status', 'owner_id', 'tenant_id', 'check_in_time', 'create_by'],
  rooms.map((r) => [r.id, 1, r.communityId, r.buildingId, r.unitId, q(r.roomCode), q(r.roomNo), r.floor, r.roomType, r.buildArea, r.innerArea, r.publicArea, q(r.orientation), r.decoration, r.status, r.ownerId, r.tenantId, d(r.checkInTime), q('system')]));
prop += insert('property_owner', ['id', 'company_id', 'owner_code', 'owner_name', 'gender', 'id_card', 'id_card_mask', 'phone', 'phone_mask', 'address', 'owner_type', 'emergency_contact', 'emergency_phone', 'status', 'create_by'],
  owners.map((o) => [o.id, 1, q(o.code), q(o.name), o.gender, q(o.idCardEnc), q(o.idCardMask), q(o.phoneEnc), q(o.phoneMask), q(o.address), o.ownerType, q(o.emergency), q(o.emergencyPhone), 1, q('system')]));
prop += insert('property_family_member', ['id', 'company_id', 'owner_id', 'member_name', 'relation', 'gender', 'phone', 'phone_mask', 'is_live', 'create_by'],
  familyMembers.map((m) => [m.id, 1, m.ownerId, q(m.name), q(m.relation), m.gender, m.phoneEnc ? q(m.phoneEnc) : 'NULL', m.phoneMask ? q(m.phoneMask) : 'NULL', 1, q('system')]));
prop += insert('property_owner_room', ['id', 'company_id', 'owner_id', 'room_id', 'relation_type', 'purchase_date', 'purchase_price', 'check_in_date', 'status', 'create_by'],
  ownerRooms.map((or, i) => {
    const room = rooms.find((r) => r.id === or.roomId);
    const sc = saleContracts[i];
    return [i + 1, 1, or.ownerId, or.roomId, 1, d(sc.contractDate), sc.salePrice, d(room.checkInTime ? room.checkInTime.slice(0, 10) : null), 1, q('system')];
  }));
prop += insert('property_sale_contract', ['id', 'company_id', 'contract_no', 'room_id', 'owner_id', 'contract_date', 'sale_price', 'pay_type', 'down_payment', 'loan_amount', 'delivery_date', 'delivery_status', 'status', 'create_by'],
  saleContracts.map((s) => [s.id, 1, q(s.no), s.roomId, s.ownerId, d(s.contractDate), s.salePrice, s.payType, s.downPayment, s.loanAmount, d(s.deliveryDate), s.deliveryStatus, s.status, q('system')]));
prop += insert('property_tenant', ['id', 'company_id', 'tenant_code', 'tenant_name', 'gender', 'id_card', 'id_card_mask', 'phone', 'phone_mask', 'company_name', 'status', 'create_by'],
  tenants.map((t) => [t.id, 1, q(t.code), q(t.name), t.gender, q(t.idCardEnc), q(t.idCardMask), q(t.phoneEnc), q(t.phoneMask), t.company ? q(t.company) : 'NULL', 1, q('system')]));
prop += insert('property_lease_contract', ['id', 'company_id', 'contract_no', 'room_id', 'tenant_id', 'lease_type', 'start_date', 'end_date', 'rent_amount', 'deposit', 'pay_cycle', 'status', 'terminate_date', 'terminate_reason', 'create_by'],
  leaseContracts.map((c) => [c.id, 1, q(c.no), c.roomId, c.tenantId, c.leaseType, d(c.startDate), d(c.endDate), c.rentAmount, c.deposit, c.payCycle, c.status, d(c.terminateDate || null), d(c.terminateReason || null), q('system')]));
prop += insert('property_check_record', ['id', 'company_id', 'room_id', 'owner_id', 'check_type', 'check_date', 'check_result', 'problems', 'status', 'remark', 'create_by'],
  checkRecords.map((c) => [c.id, 1, c.roomId, c.ownerId, c.checkType, d(c.checkDate), c.checkResult, c.problems ? q(c.problems) : 'NULL', c.status, q(c.remark), q('system')]));
prop += insert('property_decoration_record', ['id', 'company_id', 'room_id', 'owner_id', 'apply_date', 'start_date', 'end_date', 'decoration_company', 'contact_name', 'contact_phone', 'deposit', 'deposit_status', 'check_result', 'status', 'remark', 'create_by'],
  decorationRecords.map((x) => [x.id, 1, x.roomId, x.ownerId, d(x.applyDate), d(x.startDate), d(x.endDate), q(x.company), q(x.contact), q(x.contactPhone), x.deposit, x.depositStatus, x.checkResult == null ? 'NULL' : x.checkResult, x.status, q(x.remark), q('system')]));
prop += insert('finance_fee_item', ['id', 'company_id', 'community_id', 'fee_name', 'fee_code', 'fee_type', 'charge_mode', 'unit_price', 'unit', 'billing_cycle', 'is_ladder', 'is_active', 'create_by'],
  feeItems.map((f) => [f.id, 1, 1, q(f.name), q(f.code), f.type, f.mode, f.price, q(f.unit), f.cycle, f.ladder, 1, q('system')]));
prop += insert('finance_ladder_config', ['id', 'company_id', 'fee_item_id', 'ladder_name', 'min_value', 'max_value', 'unit_price', 'sort', 'create_by'],
  ladderConfigs.map((l) => [l.id, 1, l.feeItemId, q(l.name), l.min, l.max == null ? 'NULL' : l.max, l.price, l.sort, q('system')]));
prop += insert('finance_ledger', ['id', 'company_id', 'community_id', 'room_id', 'owner_id', 'fee_item_id', 'ledger_month', 'amount', 'paid_amount', 'discount_amount', 'late_fee', 'status', 'due_date', 'pay_time', 'create_by'],
  ledgers.map((l) => [l.id, 1, 1, l.roomId, l.ownerId, 1, q(l.month), l.amount, l.paid, l.discount, l.lateFee, l.status, d(l.dueDate), d(l.payTime), q('system')]));
prop += insert('finance_payment', ['id', 'company_id', 'payment_no', 'room_id', 'owner_id', 'total_amount', 'actual_amount', 'discount_amount', 'pay_type', 'pay_time', 'receipt_no', 'cashier_id', 'cashier_name', 'status', 'audit_status', 'create_by'],
  payments.map((p) => [p.id, 1, q(p.no), p.roomId, p.ownerId, p.total, p.actual, p.discount, p.payType, d(p.payTime), q(p.receipt), p.cashier.id, q(p.cashier.name), p.status, p.audit, q('system')]));
prop += insert('finance_payment_detail', ['id', 'company_id', 'payment_id', 'ledger_id', 'fee_item_id', 'ledger_month', 'amount', 'actual_amount', 'discount_amount', 'late_fee'],
  paymentDetails.map((x) => [x.id, 1, x.paymentId, x.ledgerId, x.feeItemId, q(x.month), x.amount, x.actual, x.discount, x.lateFee]));
prop += insert('finance_parking_space', ['id', 'company_id', 'community_id', 'parking_no', 'parking_type', 'parking_area', 'owner_id', 'tenant_id', 'status', 'sale_price', 'sale_date', 'rent_price', 'rent_start_date', 'rent_end_date', 'create_by'],
  parkings.map((p) => [p.id, 1, 1, q(p.no), p.type, p.area, p.ownerId, p.tenantId, p.status, p.salePrice, d(p.saleDate), p.rentPrice, d(p.rentStart), d(p.rentEnd), q('system')]));
prop += insert('finance_parking_payment', ['id', 'company_id', 'parking_id', 'payment_no', 'fee_month', 'fee_type', 'amount', 'actual_amount', 'pay_time', 'pay_type', 'status', 'remark', 'create_by'],
  parkingPayments.map((p) => [p.id, 1, p.parkingId, q(p.no), q(p.month), p.feeType, p.amount, p.actual, d(p.payTime), p.payType, p.status, p.remark ? q(p.remark) : 'NULL', q('system')]));
prop += insert('finance_prepayment', ['id', 'company_id', 'owner_id', 'amount', 'used_amount', 'balance', 'pay_time', 'pay_type', 'payment_no', 'status', 'remark', 'create_by'],
  prepays.map((p) => [p.id, 1, p.ownerId, p.amount, p.used, p.balance, d(p.payTime), p.payType, q(p.no), p.status, q(p.remark), q('system')]));
prop += insert('finance_prepayment_usage', ['id', 'company_id', 'prepayment_id', 'payment_id', 'amount', 'use_time', 'create_by'],
  prepayUsages.map((u) => [u.id, 1, u.prepaymentId, u.paymentId, u.amount, d(u.useTime), q('system')]));
prop += insert('finance_meter_reading', ['id', 'company_id', 'room_id', 'meter_type', 'meter_no', 'reading_month', 'last_reading', 'current_reading', 'usage_amount', 'reading_user', 'reading_date', 'create_by'],
  meterReadings.map((m) => [m.id, 1, m.roomId, m.meterType, q(m.meterNo), q(m.month), m.last, m.current, m.usage, q(m.user), d(m.date), q('system')]));
prop += insert('finance_late_fee_config', ['id', 'company_id', 'community_id', 'fee_item_id', 'grace_days', 'rate_type', 'rate', 'max_amount', 'is_active', 'create_by'],
  lateFeeConfig.map((c) => [c.id, 1, 1, 'NULL', c.graceDays, c.rateType, c.rate, c.max, c.active, q('system')]));
prop += insert('finance_invoice', ['id', 'company_id', 'invoice_no', 'invoice_type', 'user_id', 'user_name', 'status', 'use_time', 'void_time', 'void_reason', 'create_by'],
  invoices.map((v) => [v.id, 1, q(v.no), v.type, v.userId, v.userName ? q(v.userName) : 'NULL', v.status, d(v.useTime), d(v.voidTime), v.voidReason ? q(v.voidReason) : 'NULL', q('system')]));

// （小区行已在上方直接插入，contact_phone 为 EncryptUtils 同款密文）
writeFileSync(join(OUT_DIR, 'smart_property_property-seed.sql'), prop);
console.log(`property seed ok (rooms=${rooms.length}, owners=${owners.length}, ledgers=${ledgers.length}, payments=${payments.length})`);

// =====================================================================
// 运营管理库
// =====================================================================
let op = `-- ============================================================
-- SmartProperty 演示种子数据 - 运营管理库（由 scripts/seed/generate-seed.mjs 生成）
-- 场景：云栖湾小区运营台账（工单/保洁/消防/保安/行政/业委会），幂等可重复执行
-- 保洁/保安人员使用员工工号（9011+），非系统账号
-- ============================================================
USE \`smart_property_operation\`;\nSET NAMES utf8mb4;\n\n`;
const opDel = (t, where) => `DELETE FROM ${t} WHERE ${where};\n`;
op += ['operation_service_order|community_id=1', 'operation_order_flow|company_id=1', 'operation_clean_arrange|community_id=1',
  'operation_clean_check|community_id=1', 'operation_fire_facility|community_id=1', 'operation_fire_patrol|community_id=1',
  'operation_fire_drill|community_id=1', 'operation_security_arrange|community_id=1', 'operation_duty_record|community_id=1',
  'operation_visit_record|community_id=1', 'operation_vehicle_record|community_id=1', 'operation_goods_record|community_id=1',
  'operation_greenery|community_id=1', 'operation_greenery_check|community_id=1', 'operation_community_activity|community_id=1',
  'admin_notice|company_id=1', 'admin_notice_read|company_id=1', 'admin_message|company_id=1',
  'admin_opinion_box|company_id=1', 'admin_opinion_submit|company_id=1', 'admin_survey|company_id=1',
  'admin_survey_option|company_id=1', 'admin_survey_vote|company_id=1', 'admin_committee_member|company_id=1',
  'admin_committee_meeting|company_id=1', 'admin_regulation|company_id=1',
].map((x) => opDel(...x.split('|'))).join('') + '\n';

op += insert('operation_service_order', ['id', 'company_id', 'community_id', 'order_no', 'order_type', 'title', 'content', 'room_id', 'owner_id', 'owner_name', 'owner_phone', 'priority', 'status', 'assign_user_id', 'assign_user_name', 'assign_time', 'handle_content', 'handle_time', 'visit_content', 'visit_score', 'visit_time', 'close_time', 'create_by', 'create_time'],
  opOrders.map((o) => [o.id, 1, 1, q(o.no), o.type, q(o.title), q(o.content), o.roomId, o.ownerId, q(o.ownerName), q(o.ownerPhone), o.priority, o.status, o.assignUserId, o.assignUserName ? q(o.assignUserName) : 'NULL', d(o.assignTime), o.handleContent ? q(o.handleContent) : 'NULL', d(o.handleTime), o.visitContent ? q(o.visitContent) : 'NULL', o.visitScore, d(o.visitTime), d(o.closeTime), q(o.createBy), d(o.createTime)]));
op += insert('operation_order_flow', ['id', 'company_id', 'order_id', 'flow_type', 'content', 'operator_id', 'operator_name', 'create_time'],
  orderFlows.map((f) => [f.id, 1, f.orderId, f.flowType, f.content ? q(f.content) : 'NULL', f.operatorId, q(f.operatorName), d(f.time)]));
op += insert('operation_clean_arrange', ['id', 'company_id', 'community_id', 'area_name', 'clean_type', 'arrange_date', 'start_time', 'end_time', 'cleaner_id', 'cleaner_name', 'status', 'complete_time', 'create_by'],
  cleanArranges.map((c) => [c.id, 1, 1, q(c.area), c.cleanType, d(c.arrangeDate), d(c.startTime), d(c.endTime), c.cleanerId, q(c.cleanerName), c.status, d(c.completeTime), q('system')]));
op += insert('operation_clean_check', ['id', 'company_id', 'community_id', 'arrange_id', 'check_date', 'area_name', 'check_result', 'score', 'problems', 'checker_id', 'checker_name', 'create_by'],
  cleanChecks.map((c) => [c.id, 1, 1, c.arrangeId, d(c.checkDate), q(c.areaName), c.checkResult, c.score, c.problems ? q(c.problems) : 'NULL', c.checkerId, q(c.checkerName), q('system')]));
op += insert('operation_fire_facility', ['id', 'company_id', 'community_id', 'building_id', 'facility_name', 'facility_type', 'facility_no', 'location', 'install_date', 'expire_date', 'status', 'last_check_date', 'next_check_date', 'create_by'],
  fireFacilities.map((f) => [f.id, 1, 1, f.b, q(f.name), f.type, q(f.no), q(f.location), d(f.install), d(f.expire), f.status, d(f.lastCheck), d(f.nextCheck), q('system')]));
op += insert('operation_fire_patrol', ['id', 'company_id', 'community_id', 'patrol_date', 'patrol_time', 'patrol_area', 'patrol_result', 'problems', 'patrol_user_id', 'patrol_user_name', 'handle_content', 'status', 'create_by'],
  firePatrols.map((p) => [p.id, 1, 1, d(p.date), d(p.time), q(p.area), p.result, p.problems ? q(p.problems) : 'NULL', p.userId, q(p.userName), p.handle ? q(p.handle) : 'NULL', p.status, q('system')]));
op += insert('operation_fire_drill', ['id', 'company_id', 'community_id', 'drill_name', 'drill_type', 'drill_date', 'start_time', 'end_time', 'location', 'participant_count', 'drill_content', 'drill_summary', 'organizer', 'status', 'create_by'],
  fireDrills.map((x) => [x.id, 1, 1, q(x.name), x.type, d(x.date), d(x.start), d(x.end), q(x.location), x.count, q(x.content), x.summary ? q(x.summary) : 'NULL', q(x.organizer), x.status, q('system')]));
op += insert('operation_security_arrange', ['id', 'company_id', 'community_id', 'arrange_date', 'shift_type', 'start_time', 'end_time', 'position', 'security_id', 'security_name', 'status', 'create_by'],
  securityArranges.map((s) => [s.id, 1, 1, d(s.date), s.shiftType, d(s.start), d(s.end), q(s.position), s.securityId, q(s.securityName), s.status, q('system')]));
op += insert('operation_duty_record', ['id', 'company_id', 'community_id', 'duty_date', 'start_time', 'end_time', 'position', 'security_id', 'security_name', 'duty_content', 'abnormal_info', 'status', 'create_by'],
  dutyRecords.map((x) => [x.id, 1, 1, d(x.date), d(x.start), d(x.end), q(x.position), x.securityId, q(x.securityName), q(x.content), x.abnormal ? q(x.abnormal) : 'NULL', x.status, q('system')]));
op += insert('operation_visit_record', ['id', 'company_id', 'community_id', 'visitor_name', 'visitor_phone', 'visit_reason', 'visit_target', 'room_id', 'visit_time', 'leave_time', 'visitor_count', 'plate_no', 'guard_id', 'guard_name', 'status', 'create_by'],
  visitRecords.map((v) => [v.id, 1, 1, q(v.name), q(v.phone), q(v.reason), q(v.target), v.roomId, d(v.visitTime), d(v.leaveTime), v.count, v.plate ? q(v.plate) : 'NULL', v.guardId, q(v.guardName), v.status, q('system')]));
op += insert('operation_vehicle_record', ['id', 'company_id', 'community_id', 'plate_no', 'vehicle_type', 'record_type', 'record_time', 'gate_name', 'parking_id', 'is_temporary', 'fee_amount', 'pay_status', 'pay_time'],
  vehicleRecords.map((v) => [v.id, 1, 1, q(v.plate), v.vehicleType, v.recordType, d(v.recordTime), q(v.gate), v.parkingId, v.isTemp, v.fee, v.payStatus == null ? 'NULL' : v.payStatus, d(v.payTime)]));
op += insert('operation_goods_record', ['id', 'company_id', 'community_id', 'record_type', 'goods_name', 'goods_desc', 'quantity', 'owner_name', 'room_id', 'operator_name', 'operator_phone', 'operate_time', 'guard_id', 'guard_name', 'create_by'],
  goodsRecords.map((g) => [g.id, 1, 1, g.recordType, q(g.goods), q(g.desc), g.quantity, q(g.ownerName), g.roomId, q(g.operator), q(g.operatorPhone), d(g.time), g.guardId, q(g.guardName), q('system')]));
op += insert('operation_greenery', ['id', 'company_id', 'community_id', 'greenery_name', 'greenery_type', 'location', 'quantity', 'plant_date', 'status', 'remark', 'create_by'],
  greenery.map((g) => [g.id, 1, 1, q(g.name), g.type, q(g.location), g.quantity, d(g.plant), g.status, g.remark ? q(g.remark) : 'NULL', q('system')]));
op += insert('operation_greenery_check', ['id', 'company_id', 'community_id', 'check_date', 'area_name', 'check_result', 'score', 'problems', 'checker_id', 'checker_name', 'create_by'],
  greeneryChecks.map((c) => [c.id, 1, 1, d(c.date), q(c.area), c.result, c.score, c.problems ? q(c.problems) : 'NULL', c.checkerId, q(c.checkerName), q('system')]));
op += insert('operation_community_activity', ['id', 'company_id', 'community_id', 'activity_name', 'activity_type', 'activity_date', 'start_time', 'end_time', 'location', 'content', 'participant_count', 'budget', 'actual_cost', 'organizer', 'status', 'create_by'],
  activities.map((a) => [a.id, 1, 1, q(a.name), a.type, d(a.date), d(a.start), d(a.end), q(a.location), q(a.content), a.count, a.budget, a.cost, q(a.organizer), a.status, q('system')]));
op += insert('admin_notice', ['id', 'company_id', 'community_id', 'notice_title', 'notice_content', 'notice_type', 'is_top', 'is_publish', 'publish_time', 'expire_time', 'read_count', 'status', 'create_by'],
  notices.map((n) => [n.id, 1, 1, q(n.title), q(n.content), n.type, n.top, n.status === 2 ? 1 : 0, d(n.publish), d(n.expire), n.read, n.status, q('system')]));
op += insert('admin_notice_read', ['company_id', 'notice_id', 'user_id', 'read_time'],
  [1, 2, 3].flatMap((n) => STAFF.map((s) => [1, n, s.id, q('2026-09-05 09:00:00')])));
op += insert('admin_message', ['id', 'company_id', 'message_type', 'title', 'content', 'sender_id', 'sender_name', 'receiver_id', 'receiver_name', 'is_read', 'send_status', 'send_time', 'business_type', 'business_id'],
  messages.map((m) => [m.id, 1, m.type, q(m.title), q(m.content), 2, q('张伟民'), m.receiver, q(m.receiverName), 0, 1, d('2026-09-05 09:00:00'), m.businessType ? q(m.businessType) : 'NULL', m.businessId]));
op += insert('admin_opinion_box', ['id', 'company_id', 'community_id', 'box_name', 'admin_user_id', 'admin_user_name', 'is_anonymous', 'is_active', 'remark', 'create_by'],
  opinionBoxes.map((b) => [b.id, 1, 1, q(b.name), b.admin, q(b.adminName), b.anonymous, b.active, q(b.remark), q('system')]));
op += insert('admin_opinion_submit', ['id', 'company_id', 'box_id', 'title', 'content', 'is_anonymous', 'submit_user_name', 'submit_time', 'status', 'reply_content', 'reply_user_id', 'reply_user_name', 'reply_time', 'satisfaction', 'create_by'],
  opinions.map((o) => [o.id, 1, o.box, q(o.title), q(o.content), o.anonymous, o.user ? q(o.user) : 'NULL', d(`2026-08-${pad(rint(10, 28), 2)} 10:00:00`), o.status, o.reply ? q(o.reply) : 'NULL', o.reply ? 5 : 'NULL', o.reply ? q(o.replyUser) : 'NULL', o.reply ? d('2026-09-01 15:00:00') : 'NULL', o.satisfaction, q('system')]));
op += insert('admin_survey', ['id', 'company_id', 'community_id', 'survey_title', 'survey_desc', 'survey_type', 'start_time', 'end_time', 'is_anonymous', 'is_multiple', 'participant_count', 'status', 'create_by'],
  surveys.map((s) => [s.id, 1, 1, q(s.title), q(s.desc), s.type, d(s.start), d(s.end), s.anonymous, s.multiple, s.participants, s.status, q('system')]));
op += insert('admin_survey_option', ['id', 'company_id', 'survey_id', 'option_content', 'option_order', 'vote_count'],
  surveys.flatMap((s) => s.options.map((o) => [o.id, 1, s.id, q(o.content), o.id, o.votes])));
op += insert('admin_survey_vote', ['id', 'company_id', 'survey_id', 'option_id', 'vote_time'],
  surveyVotes.map((v) => [v.id, 1, v.surveyId, v.optionId, d(v.voteTime)]));
op += insert('admin_committee_member', ['id', 'company_id', 'community_id', 'member_name', 'position', 'phone', 'room_id', 'term_start', 'term_end', 'introduction', 'status', 'create_by'],
  committeeMembers.map((m) => [m.id, 1, 1, q(m.name), q(m.position), q(m.phone), m.roomId, d(m.termStart), d(m.termEnd), q(m.introduction), m.status, q('system')]));
op += insert('admin_committee_meeting', ['id', 'company_id', 'community_id', 'meeting_title', 'meeting_date', 'start_time', 'end_time', 'location', 'meeting_content', 'meeting_summary', 'attendees', 'status', 'create_by'],
  committeeMeetings.map((m) => [m.id, 1, 1, q(m.title), d(m.date), d(m.start), d(m.end), q(m.location), q(m.content), m.summary ? q(m.summary) : 'NULL', q(m.attendees), m.status, q('system')]));
op += insert('admin_regulation', ['id', 'company_id', 'title', 'content', 'category', 'is_publish', 'publish_time', 'view_count', 'status', 'create_by'],
  regulations.map((r) => [r.id, 1, q(r.title), q(r.content), q(r.category), r.isPublish, d(r.publishTime), r.viewCount, r.status, q('system')]));

writeFileSync(join(OUT_DIR, 'smart_property_operation-seed.sql'), op);
console.log(`operation seed ok (orders=${opOrders.length}, flows=${orderFlows.length}, vehicles=${vehicleRecords.length}, votes=${surveyVotes.length})`);

